package cn.wolfcode.service.impl;

import cn.wolfcode.common.exception.BusinessException;
import cn.wolfcode.common.web.CommonCodeMsg;
import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.*;
import cn.wolfcode.mapper.OrderInfoMapper;
import cn.wolfcode.mapper.PayLogMapper;
import cn.wolfcode.mapper.RefundLogMapper;
import cn.wolfcode.mapper.SeckillProductMapper;
import cn.wolfcode.mq.OrderMessage;
import cn.wolfcode.redis.SeckillRedisKey;
import cn.wolfcode.service.IOrderInfoService;
import cn.wolfcode.service.ISeckillProductService;
import cn.wolfcode.util.IdGenerateUtil;
import cn.wolfcode.web.feign.OrderPayIntegralFeignApi;
import cn.wolfcode.web.feign.OrderPayOnlineFeignApi;
import cn.wolfcode.web.msg.SeckillCodeMsg;
import com.alibaba.fastjson.JSON;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by wolfcode-lanxw
 */
@Service
public class OrderInfoSeviceImpl implements IOrderInfoService {
    @Autowired
    private ISeckillProductService seckillProductService;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PayLogMapper payLogMapper;
    @Autowired
    private RefundLogMapper refundLogMapper;
    @Resource
    private SeckillProductMapper seckillProductMapper;
    @Resource
    private OrderPayOnlineFeignApi orderPayOnlineFeignApi;

    @Override
    public OrderInfo findByPhoneAndSeckillId(String phone, Long seckillId) {
        return orderInfoMapper.findByPhotoAndSeckillId(phone, seckillId);
    }

    /**
     * 创建秒杀订单
     * @param phone
     * @param seckillProductVo
     * @return
     */
    @Transactional
    @Override
    public OrderInfo doSeckill(String phone, SeckillProductVo seckillProductVo) {
        // 模拟秒杀时出现了异常，进行订单数据回补
//        int i = 1 / 0;
        // 5. 扣减库存数量
        // 超卖的最后一层保证（数据库方面层次的保证，乐观锁）
        if (seckillProductService.decrStockCount(seckillProductVo.getId()) == 0) {
            throw new BusinessException(SeckillCodeMsg.SECKILL_STOCK_OVER); // 商品抢完了
        }
        // 4. 创建秒杀订单
        OrderInfo orderInfo = createOrderInfo(phone, seckillProductVo);
        // 将订单信息存储到redis - set中
        //       key                                 value
        // seckillOrderSet:秒杀商品ID                 phone （手机号）
        // 将同步到redis的操作改进到canal中执行
//        String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(seckillProductVo.getId()));
//        redisTemplate.opsForSet().add(orderSetKey, phone);
        return orderInfo;
    }

    @Override
    public OrderInfo findByOrderNo(Long orderNo) {
        // 从redis中去查询订单信息
        String orderHashRealKey = SeckillRedisKey.SECKILL_ORDER_HASH.getRealKey("");
        String s = (String) redisTemplate.opsForHash().get(orderHashRealKey, String.valueOf(orderNo));
        return JSON.parseObject(s, OrderInfo.class);
//        return orderInfoMapper.find(String.valueOf(orderNo));
    }

    /**
     * 取消订单
     * @param orderNo
     */
    @Override
    @Transactional
    public void cancleOrder(String orderNo) {
        System.out.println("超时取消订单开始...");
        OrderInfo orderInfo = orderInfoMapper.find(orderNo);
        // 未付款
        if (OrderInfo.STATUS_ARREARAGE.equals(orderInfo.getStatus())) {
            // 修改订单状态
            int i = orderInfoMapper.updateCancelStatus(orderNo, OrderInfo.STATUS_TIMEOUT);// 超时取消订单
            if (i == 0) {
                return; // 订单状态没有修改成功就不用继续往下走了
            }
            // 回补真实库存
            seckillProductService.incrStockCount(orderInfo.getSeckillId());
            // 回补redis库存
            seckillProductService.syncStockToRedis(orderInfo.getSeckillTime(), orderInfo.getSeckillId());
        }
        System.out.println("超时取消订单结束...");
    }

    @Value("${pay.returnUrl}")
    private String returnUrl;
    @Value("${pay.notifyUrl}")
    private String notifyUrl;
    // 在线支付
    @Override
    public Result<String> payOrderOnline(String orderNo) {
        // 根据订单编号查询出订单信息
        OrderInfo orderInfo = orderInfoMapper.find(orderNo);
        if (orderInfo == null) {
            return Result.error(CommonCodeMsg.ILLEGAL_OPERATION);
        }
        if (OrderInfo.STATUS_ARREARAGE.equals(orderInfo.getStatus())) {
            // 未付款
            PayVo payVo = new PayVo();
            payVo.setOutTradeNo(orderInfo.getOrderNo());
            payVo.setTotalAmount(String.valueOf(orderInfo.getSeckillPrice()));
            payVo.setSubject(orderInfo.getProductName());
            payVo.setBody(orderInfo.getProductName());
            payVo.setReturnUrl(returnUrl);
            payVo.setNotifyUrl(notifyUrl);
            return orderPayOnlineFeignApi.payOnline(payVo);
        }
        return Result.error(SeckillCodeMsg.PAY_STATUS_CHANGE);
    }

    @Resource
    private OrderPayIntegralFeignApi orderPayIntegralFeignApi;

    // 积分支付
    @Override
    @GlobalTransactional
    public Result<String> payOrderIntegral(String orderNo) {
        // 查出订单信息
        OrderInfo orderInfo = orderInfoMapper.find(orderNo);
        // 未付款
        if (OrderInfo.PAYTYPE_ONLINE.equals(orderInfo.getStatus())) {
            // 插入支付日志信息
            PayLog payLog = new PayLog();
            payLog.setOrderNo(orderInfo.getOrderNo());
            payLog.setPayTime(new Date());
            payLog.setPayType(1);
            payLog.setTotalAmount(String.valueOf(orderInfo.getIntergral()));
            payLogMapper.insert(payLog);
            // 调用远程服务实现积分扣减
            OperateIntergralVo vo = new OperateIntergralVo();
            vo.setUserId(orderInfo.getUserId());
            vo.setValue(orderInfo.getIntergral());
            Result result = orderPayIntegralFeignApi.payIntegral(vo);
            if (result == null || result.hasError()) {
                throw new BusinessException(SeckillCodeMsg.INTERGRAL_SERVER_ERROR);
            }
            // 修改订单状态
            int effectCount = orderInfoMapper.changePayStatus(orderNo, OrderInfo.STATUS_ACCOUNT_PAID, OrderInfo.PAYTYPE_INTERGRAL);
            if (effectCount == 0) {
                throw new BusinessException(SeckillCodeMsg.PAY_ERROR);
            }
        }
        return Result.success();
    }

    /**
     * 修改订单状态
     * @param orderNo
     * @param status
     * @param payType
     * @return
     */
    @Override
    public int changePayStatus(String orderNo, Integer status, int payType) {
        return orderInfoMapper.changePayStatus(orderNo, status, payType);
    }

    /**
     * 在线退款
     * @param orderInfo
     */
    @Override
    public void refundOnline(OrderInfo orderInfo) {
        RefundVo vo = new RefundVo();
        vo.setOutTradeNo(orderInfo.getOrderNo());
        vo.setRefundAmount(String.valueOf(orderInfo.getSeckillPrice()));
        vo.setRefundReason("不想要了....");
        Result<Boolean> result = orderPayOnlineFeignApi.refundOnline(vo);
        if (result == null || result.hasError() || !result.getData()) {
            throw new BusinessException(SeckillCodeMsg.REFUND_ERROR);
        }
        // 修改订单状态
        orderInfoMapper.changeRefundStatus(orderInfo.getOrderNo(), OrderInfo.STATUS_REFUND);
    }

    @Override
    @GlobalTransactional
    public Result<String> refundIntegral(OrderInfo orderInfo) {
        // 已付款
        if (OrderInfo.STATUS_ACCOUNT_PAID.equals(orderInfo.getStatus())) {
            // 添加退款日志
            RefundLog refundLog = new RefundLog();
            refundLog.setOrderNo(orderInfo.getOrderNo());
            refundLog.setRefundTime(orderInfo.getSeckillDate());
            refundLog.setRefundType(OrderInfo.PAYTYPE_INTERGRAL);
            refundLog.setRefundReason("不想要了...");
            refundLog.setRefundAmount(orderInfo.getIntergral());
            refundLogMapper.insert(refundLog);
            // 调用远程服务增加积分，实现退款
            OperateIntergralVo vo = new OperateIntergralVo();
            vo.setUserId(orderInfo.getUserId());
            vo.setValue(orderInfo.getIntergral());
            Result<String> result = orderPayIntegralFeignApi.incrIntegral(vo);
            if (result == null || result.hasError()) {
                throw new BusinessException(SeckillCodeMsg.INTERGRAL_SERVER_ERROR);
            }
            // 修改订单状态(已付款 -> 已退款)
            int effectCount = orderInfoMapper.changeRefundStatus(orderInfo.getOrderNo(), OrderInfo.STATUS_REFUND);
            if (effectCount == 0) {
                throw new BusinessException(SeckillCodeMsg.REFUND_ERROR);
            }
//            int i = 1 / 0;
        }

        return Result.success();
    }

    // 创建订单
    private OrderInfo createOrderInfo(String phone, SeckillProductVo seckillProductVo) {
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(seckillProductVo, orderInfo);
        orderInfo.setUserId(Long.parseLong(phone)); // 用户id
        orderInfo.setCreateDate(new Date()); // 订单创建·时间
        orderInfo.setDeliveryAddrId(1L); // 收获地址
        orderInfo.setSeckillDate(seckillProductVo.getStartDate()); // 秒杀商品的日期
        orderInfo.setSeckillTime(seckillProductVo.getTime()); // 秒杀场次
        orderInfo.setOrderNo(String.valueOf(IdGenerateUtil.get().nextId())); // 订单编号（雪花算法）
        orderInfo.setSeckillId(seckillProductVo.getId()); // 秒杀id
        orderInfoMapper.insert(orderInfo);
        return orderInfo;
    }
}

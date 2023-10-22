package cn.wolfcode.web.controller;

import cn.wolfcode.common.constants.CommonConstants;
import cn.wolfcode.common.web.CommonCodeMsg;
import cn.wolfcode.common.web.Result;
import cn.wolfcode.common.web.anno.RequireLogin;
import cn.wolfcode.domain.OrderInfo;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.mq.MQConstant;
import cn.wolfcode.mq.OrderMessage;
import cn.wolfcode.redis.SeckillRedisKey;
import cn.wolfcode.service.IOrderInfoService;
import cn.wolfcode.service.ISeckillProductService;
import cn.wolfcode.util.DateUtil;
import cn.wolfcode.util.UserUtil;
import cn.wolfcode.web.msg.SeckillCodeMsg;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by lanxw
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderInfoController {
    @Autowired
    private ISeckillProductService seckillProductService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private IOrderInfoService orderInfoService;

    // http://localhost:9000/seckill/order/doSeckill

    /**
     * 1.0 qps 500
     * 2.0 qps 3000
     * 流程：
     * 1. 判断是否在抢购时间段（redis优化）
     * 2. 判断是否重复下单（redis优化），真正保证不会出现重复下单的是在数据库层面对user_id和seckill_id创建唯一索引
     * 3. 保证秒杀商品库存足够（redis控制访问数据库人数，数据库底层乐观锁机制实现最后一层保证）
     * 4. 创建秒杀商品订单信息（将信息存储到redis进行优化）
     * @param time
     * @param seckillId
     * @param request
     * @return
     */
    @RequireLogin
    @PostMapping("/doSeckill")
    public Result<String> handleOrderInfo(@RequestParam("time") Integer time, @RequestParam("seckillId") Long seckillId, HttpServletRequest request) {
        // 1. 判断此时是否在抢购时间
        SeckillProductVo seckillProductVo = seckillProductService.findDetailsCache(time, seckillId);
        // TODO 测试时没有放行，正式的逻辑需要放开判断逻辑
//        if (!DateUtil.isLegalTime(seckillProductVo.getStartDate(), seckillProductVo.getTime())) {
//            return Result.error(CommonCodeMsg.ILLEGAL_OPERATION); // 为在抢购时间进到这里来就是非法用户进行非法操作，提示非法操作
//        }
        // 2. 确保一个用户只能够抢购一次（查看order_info表中是否已经又该用户的该抢购记录）
        // 获取token信息
        String token = request.getHeader(CommonConstants.TOKEN_NAME);
        // 根据token从redis中获取photo信息
        String phone = UserUtil.getUserPhone(redisTemplate, token);
        // 根据手机号和秒杀id查询订单信息
        OrderInfo orderInfo = orderInfoService.findByPhoneAndSeckillId(phone, seckillId);
//        if (orderInfo != null) {
//            return Result.error(SeckillCodeMsg.SECKILL_STOCK_OVER);
//        }
        String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(seckillId));
        // 2.0 通过查询redis中是否该用户已经抢到该秒杀商品来判断是否重复下单
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(orderSetKey, phone))) { // 2.0
//        if (orderInfo != null) { // 1.0
            // 重复下单
            return Result.error(SeckillCodeMsg.REPEAT_SECKILL);
        }
        // 3. 保证库存足够
        // 2.0 使用redis控制秒杀请求的人数（真正进入数据库的请求）
        String seckillStockCountKey = SeckillRedisKey.SECKILL_STOCK_COUNT_HASH.getRealKey(String.valueOf(time));
        Long remandCount = redisTemplate.opsForHash().increment(seckillStockCountKey, String.valueOf(seckillId), -1);
        if (remandCount < 0) {
            return Result.error(SeckillCodeMsg.SECKILL_STOCK_OVER);
        }
        // 1.0 以下多线程下会出现超卖问题
//        if (seckillProductVo.getStockCount() <= 0) {
//            // 库存不足
//            return Result.error(SeckillCodeMsg.SECKILL_STOCK_OVER);
//        }
        // 4. 创建秒杀订单
        // 5. 扣减库存数量
        // 使用MQ进行异步下单
//        orderInfoService.doSeckill(phone, seckillProductVo);
//        return Result.success(orderInfo.getOrderNo());
        OrderMessage orderMessage = new OrderMessage(time, seckillId, token, Long.parseLong(phone));
        rocketMQTemplate.syncSend(MQConstant.ORDER_PEDDING_TOPIC, orderMessage);
        return Result.success("成功进入秒杀队列，请耐心等待结果");
    }

    // 查询订单信息
    @RequireLogin
    @GetMapping("/find")
    public Result<OrderInfo> findByOrderNo(@RequestParam("orderNo") Long orderNo, HttpServletRequest request){
        OrderInfo orderInfo = orderInfoService.findByOrderNo(orderNo);
        // 判断是否是本人在操作自己的订单
        String token = request.getHeader(CommonConstants.TOKEN_NAME);
        String phone = UserUtil.getUserPhone(redisTemplate, token);
        if (!phone.equals(String.valueOf(orderInfo.getUserId()))) {
            // 提示非法操作
            return Result.error(CommonCodeMsg.ILLEGAL_OPERATION);
        }
        // 获取对应的订单信息
        return Result.success(orderInfo);
    }

}

package cn.wolfcode.mq;

import cn.wolfcode.domain.OrderInfo;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.service.IOrderInfoService;
import cn.wolfcode.service.ISeckillProductService;
import cn.wolfcode.web.msg.SeckillCodeMsg;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

// 在秒杀商品controller和service之间使用mq，缓冲直接进入service的请求，避免数据库的高压力
// 到service之后，处理完成之后将处理结果放到结果mq中
// 成功需要返回订单编号和用户token
// 失败返回错误码、错误信息、秒杀场次、秒杀商品id
@Component
@RocketMQMessageListener(consumerGroup = "peddingGroup", topic = MQConstant.ORDER_PEDDING_TOPIC)
public class OrderPeddingQueueListener implements RocketMQListener<OrderMessage> {
    @Resource
    private IOrderInfoService orderInfoService;
    @Resource
    private ISeckillProductService seckillProductService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Override
    public void onMessage(OrderMessage orderMessage) {
        // 秒杀结果封装为OrderMQResult
        OrderMQResult result = new OrderMQResult();
        // 无论是否抢到秒杀商品，都需要返回token，需要通过token找到要通知的客户端
        result.setToken(orderMessage.getToken());
        String tag;
        try {
            SeckillProductVo vo = seckillProductService.findDetailsCache(orderMessage.getTime(), orderMessage.getSeckillId());
            OrderInfo orderInfo = orderInfoService.doSeckill(String.valueOf(orderMessage.getUserPhone()), vo);
            // 抢购成功返回订单编号，跳转订单页面，进行支付
            result.setOrderNo(orderInfo.getOrderNo());
            // 打上成功标志
            tag = MQConstant.ORDER_RESULT_SUCCESS_TAG;
            // 秒杀成功之后发送延时消息，监听订单状态
            rocketMQTemplate.syncSend(
                    MQConstant.ORDER_PAY_TIMEOUT_TOPIC,
                    MessageBuilder.withPayload(result).build(),
                    3000, // 这是发送消息的超时时间，以毫秒为单位。如果在指定的超时时间内无法成功发送消息，方法将抛出异常。可以使用这个参数来设置发送消息的最大等待时间。
                    MQConstant.ORDER_PAY_TIMEOUT_DELAY_LEVEL // 延迟十分钟发送（也就是给十分钟的付款时间）
//                    5
            );
        } catch (Exception e) {
            e.printStackTrace();
            // 抢购失败
            result.setCode(SeckillCodeMsg.SECKILL_ERROR.getCode()); // 秒杀失败错误码
            result.setMsg(SeckillCodeMsg.SECKILL_ERROR.getMsg()); // 秒杀失败提示信息
            result.setTime(orderMessage.getTime());  // 场次
            result.setSeckillId(orderMessage.getSeckillId()); // 秒杀商品id
            tag = MQConstant.ORDER_RESULT_FAIL_TAG; // 打上错误标签
        }
        // 将当前的秒杀结果存进秒杀结果mq中，进行处理（三种情况）
        rocketMQTemplate.syncSend(MQConstant.ORDER_RESULT_TOPIC + ":" + tag, result);
    }
}

package cn.wolfcode.mq;

import cn.wolfcode.service.IOrderInfoService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 订单超时监听器
 */
@Component
@RocketMQMessageListener(consumerGroup = "orderTimeoutGroup", topic = MQConstant.ORDER_PAY_TIMEOUT_TOPIC)
public class OrderPeddingTimeoutListener implements RocketMQListener<OrderMQResult> {
    @Resource
    private IOrderInfoService orderInfoService;
    @Override
    public void onMessage(OrderMQResult OrderMQResult) {
        System.out.println("超时取消订单");
        // 取消订单
        orderInfoService.cancleOrder(OrderMQResult.getOrderNo());
    }
}

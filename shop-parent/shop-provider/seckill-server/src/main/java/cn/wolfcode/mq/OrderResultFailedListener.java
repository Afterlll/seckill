package cn.wolfcode.mq;

import cn.wolfcode.service.IOrderInfoService;
import cn.wolfcode.service.ISeckillProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 秒杀失败时，进行库存回补
 */
@Component
@Slf4j
// 监听错误标签（秒杀失败）
@RocketMQMessageListener(consumerGroup = "resultFailedGroup", topic = MQConstant.ORDER_RESULT_TOPIC, selectorExpression = MQConstant.ORDER_RESULT_FAIL_TAG)
public class OrderResultFailedListener implements RocketMQListener<OrderMessage> {
    @Resource
    private ISeckillProductService seckillProductService;
    @Override
    public void onMessage(OrderMessage orderMessage) {
//        log.info("秒杀失败，进行库存回补");
        System.out.println("秒杀失败，进行库存回补");
        seckillProductService.syncStockToRedis(orderMessage.getTime(), orderMessage.getSeckillId());
    }
}

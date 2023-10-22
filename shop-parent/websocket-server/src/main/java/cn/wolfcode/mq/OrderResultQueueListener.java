package cn.wolfcode.mq;

import cn.wolfcode.ws.OrderWebSocketServer;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

// 秒杀结果mq队列处理
@Component
// tag 过滤 取 * （默认就是） 对秒杀成功和失败都要进行处理
@RocketMQMessageListener(consumerGroup = "orderResultGroup", topic = MQConstants.ORDER_RESULT_TOPIC)
public class OrderResultQueueListener implements RocketMQListener<OrderMQResult> {
    @Override
    public void onMessage(OrderMQResult orderMQResult) {
        // 由于在异步下单service处理秒杀结果之后浏览器还没有建立起websocket连接，给三次心跳时间（0.3）
        int count = 3; // 给的心跳次数机会
        Session session = null;
        while (count -- > 0) {
            session = OrderWebSocketServer.clients.get(orderMQResult.getToken());
            if (session != null) {
                // 拿到连接了，发送给客户端消息
                try {
                    // 发送秒杀结果
                    session.getBasicRemote().sendText(JSON.toJSONString(orderMQResult));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            // 没有拿到连接就进行一次心跳，等待连接
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

package cn.wolfcode.handler;

import cn.wolfcode.domain.OrderInfo;
import cn.wolfcode.redis.SeckillRedisKey;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Slf4j
@Component
@CanalTable(value = "t_order_info")
public class OrderaInfoHandler implements EntryHandler<OrderInfo> {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void insert(OrderInfo orderInfo) {
        log.info("当有数据插入的时候会触发这个方法");
        log.info("插入的数据为：{}", orderInfo);
        // 将创建的订单同步到redis中(set中)
        String orderSetKey = SeckillRedisKey.SECKILL_ORDER_SET.getRealKey(String.valueOf(orderInfo.getSeckillId()));
        redisTemplate.opsForSet().add(orderSetKey, String.valueOf(orderInfo.getUserId()));
        // 将创建的订单同步到redis中的map中去
        String orderHashRealKey = SeckillRedisKey.SECKILL_ORDER_HASH.getRealKey("");
        redisTemplate.opsForHash().put(orderHashRealKey, orderInfo.getOrderNo(), JSON.toJSONString(orderInfo));
    }

    @Override
    public void update(OrderInfo before, OrderInfo after) {
        log.info("当有数据更新的时候会触发这个方法");
        log.info("插入前：{}", before);
        log.info("插入后：{}", after);
        // 将更新的订单同步到redis中的map中去
        String orderHashRealKey = SeckillRedisKey.SECKILL_ORDER_HASH.getRealKey("");
        redisTemplate.opsForHash().put(orderHashRealKey, after.getOrderNo(), JSON.toJSONString(after));
    }

    @Override
    public void delete(OrderInfo orderInfo) {
        log.info("当有数据删除的时候会触发这个方法");
        log.info("删除的数据为：{}", orderInfo);
    }
}
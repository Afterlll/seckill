package cn.wolfcode.job;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.feign.SeckillProductFeignApi;
import cn.wolfcode.redis.JobRedisKey;
import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 每天凌晨对每天要上架的秒杀商品列表存储到redis中
 */
@Component
@Setter
@Getter
@Data
@RefreshScope
@Slf4j
public class SeckillProductJob implements SimpleJob {
    @Value("${jobCron.seckillProduct}")
    private String cron;
    @Resource
    private SeckillProductFeignApi seckillProductFeignApi;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void execute(ShardingContext shardingContext) {
        String time = shardingContext.getShardingParameter();
        // 1. 调用远程接口获取秒杀列表
        Result<List<SeckillProductVo>> result = seckillProductFeignApi.getSeckillProductList(Integer.parseInt(time));
        if (result == null || result.hasError()) { // 远程调用接口出现错误
            return;
        }
        List<SeckillProductVo> seckillProductVoList = result.getData();
        // 2. 删除redis中的数据
        // 秒杀商品列表的key
        String key = JobRedisKey.SECKILL_PRODUCT_HASH.getRealKey(time);
        // 秒杀商品数量的key
        String seckillStockCountKey = JobRedisKey.SECKILL_STOCK_COUNT_HASH.getRealKey(time);
        // 删除前一天的数据
        redisTemplate.delete(key);
        redisTemplate.delete(seckillStockCountKey);
        // 3. 保存秒杀列表和数量到redis中
        for (SeckillProductVo vo : seckillProductVoList) {
            redisTemplate.opsForHash().put(key, String.valueOf(vo.getId()), JSON.toJSONString(vo));
            redisTemplate.opsForHash().put(seckillStockCountKey, String.valueOf(vo.getId()), String.valueOf(vo.getStockCount()));
        }
    }
}

package cn.wolfcode.feign;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.feign.fallback.SeckillProductFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "seckill-service", fallback = SeckillProductFallback.class)
public interface SeckillProductFeignApi {
    @GetMapping("/seckill/getSeckillProductList")
    Result<List<SeckillProductVo>> getSeckillProductList(@RequestParam("time") Integer time);
}

package cn.wolfcode.web.feign.to;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.service.ISeckillProductService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillProductClient {

    @Resource
    private ISeckillProductService seckillProductService;

    @GetMapping("/getSeckillProductList")
    Result<List<SeckillProductVo>> getSeckillProductList(@RequestParam("time") Integer time) {
        return Result.success(seckillProductService.queryByTime(time));
    }
}

package cn.wolfcode.web.controller;

import cn.wolfcode.common.exception.BusinessException;
import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.Product;
import cn.wolfcode.domain.SeckillProduct;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.service.ISeckillProductService;
import cn.wolfcode.web.feign.ProductFeignApi;
import cn.wolfcode.web.msg.SeckillCodeMsg;
import com.mysql.cj.protocol.x.ReusableInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lanxw
 * 秒杀商品信息查询
 */
@RestController
@RequestMapping("/seckillProduct")
@Slf4j
public class SeckillProductController {
    @Autowired
    private ISeckillProductService seckillProductService;

    // 线程500 循环10
    // 1.0 qps 2000
    // 2.0 qps 4500
    // seckill/seckillProduct/queryByTime?time=10
    // 获取秒杀商品列表信息
    @GetMapping("/queryByTime")
    public Result<List<SeckillProductVo>> queryByTime(@RequestParam("time") Integer time) {
        // 1.0 直接访问数据库
//        return Result.success(seckillProductService.queryByTime(time));
        // 2.0 访问reids缓存
        return Result.success(seckillProductService.queryByTimeCache(time));
    }

    // 线程500 循环10
    // 1.0 qps 3300
    // 2.0 qps 4500
    // http://localhost:9000/seckill/seckillProduct/find?time=10&seckillId=23
    // 查询出对应描述商品的具体信息
        @GetMapping("/find")
    public Result<SeckillProductVo> findDetails(@RequestParam("time") Integer time, @RequestParam("seckillId") Long seckillId) {
        // 1.0 直接访问数据库
//        return Result.success(seckillProductService.findDetails(time, seckillId));
        // 2.0 访问reids缓存
        return Result.success(seckillProductService.findDetailsCache(time, seckillId));
    }

}

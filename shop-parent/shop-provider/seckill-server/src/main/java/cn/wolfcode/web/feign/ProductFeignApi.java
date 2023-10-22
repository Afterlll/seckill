package cn.wolfcode.web.feign;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.Product;
import cn.wolfcode.web.feign.fallback.ProductFenApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "product-service", fallback = ProductFenApiFallback.class)
public interface ProductFeignApi {
    // 调用远程商品服务，返回Result响应结果
    @RequestMapping("/product/queryByIds")
    Result<List<Product>> getProductByIds(@RequestParam List<Long> productIds);
}

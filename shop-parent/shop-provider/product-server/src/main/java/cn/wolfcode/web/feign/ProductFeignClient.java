package cn.wolfcode.web.feign;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.Product;
import cn.wolfcode.service.IProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductFeignClient {
    @Resource
    private IProductService productService;

    @RequestMapping("/queryByIds")
    Result<List<Product>> getProductByIds(@RequestParam List<Long> productIds) {
        return Result.success(productService.queryByIds(productIds));
    }
}

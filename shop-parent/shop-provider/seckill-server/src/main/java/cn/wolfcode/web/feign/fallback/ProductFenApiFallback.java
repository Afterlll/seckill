package cn.wolfcode.web.feign.fallback;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.Product;
import cn.wolfcode.web.feign.ProductFeignApi;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductFenApiFallback implements ProductFeignApi {
    @Override
    public Result<List<Product>> getProductByIds(List<Long> productIds) {
        // TODO 商品服务挂了之后需要做的处理....
        return null;
    }
}

package cn.wolfcode.feign.fallback;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.feign.SeckillProductFeignApi;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeckillProductFallback implements SeckillProductFeignApi {
    @Override
    public Result<List<SeckillProductVo>> getSeckillProductList(Integer time) {
        // TODO 通知管理员
        return null;
    }
}

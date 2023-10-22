package cn.wolfcode.web.feign.fallback;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.OperateIntergralVo;
import cn.wolfcode.web.feign.OrderPayIntegralFeignApi;
import org.springframework.stereotype.Component;

@Component
public class OrderPayIntegralFallback implements OrderPayIntegralFeignApi {
    @Override
    public Result<String> payIntegral(OperateIntergralVo vo) {
        return null;
    }

    @Override
    public Result<String> incrIntegral(OperateIntergralVo vo) {
        return null;
    }
}

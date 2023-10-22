package cn.wolfcode.web.feign.fallback;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.PayVo;
import cn.wolfcode.domain.RefundVo;
import cn.wolfcode.web.feign.OrderPayOnlineFeignApi;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderPayOnlineFallback implements OrderPayOnlineFeignApi {
    @Override
    public Result<String> payOnline(PayVo payVo) {
        return null;
    }

    @Override
    public Result<Boolean> checkV1(Map<String, String> map) {
        return null;
    }

    @Override
    public Result<Boolean> refundOnline(RefundVo refundVo) {
        return null;
    }
}

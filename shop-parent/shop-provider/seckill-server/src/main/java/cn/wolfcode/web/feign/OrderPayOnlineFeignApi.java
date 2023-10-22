package cn.wolfcode.web.feign;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.PayVo;
import cn.wolfcode.domain.RefundVo;
import cn.wolfcode.web.feign.fallback.OrderPayOnlineFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "pay-service", fallback = OrderPayOnlineFallback.class)
public interface OrderPayOnlineFeignApi {
    @RequestMapping("/alipay/online")
    Result<String> payOnline(@RequestBody PayVo payVo);
    @RequestMapping("/alipay/checkV1")
    Result<Boolean> checkV1(@RequestParam Map<String, String> map);
    @RequestMapping("/alipay/refundOnline")
    Result<Boolean> refundOnline(@RequestBody RefundVo refundVo);
}

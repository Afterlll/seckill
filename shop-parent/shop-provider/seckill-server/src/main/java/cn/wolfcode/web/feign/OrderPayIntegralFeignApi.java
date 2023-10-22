package cn.wolfcode.web.feign;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.OperateIntergralVo;
import cn.wolfcode.web.feign.fallback.OrderPayIntegralFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(value = "intergral-service", fallback = OrderPayIntegralFallback.class)
public interface OrderPayIntegralFeignApi {
    @RequestMapping("/integral/pay")
    Result<String> payIntegral(@RequestBody OperateIntergralVo vo);
    @RequestMapping("/integral/refund")
    Result<String> incrIntegral(@RequestBody OperateIntergralVo vo);
}

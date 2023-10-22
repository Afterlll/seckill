package cn.wolfcode.web.feign.to;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.OperateIntergralVo;
import cn.wolfcode.mapper.UsableIntegralMapper;
import cn.wolfcode.service.IUsableIntegralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderPayIntegralFeignClient {
    @Autowired
    private IUsableIntegralService usableIntegralService;
    @Autowired
    private UsableIntegralMapper usableIntegralMapper;
    @RequestMapping("/integral/pay")
    Result<String> payIntegral(@RequestBody OperateIntergralVo vo) {
//        usableIntegralService.decrIntergral(vo);
        usableIntegralService.decrIntergralTry(vo, null); // 传入null，aop会自动注入该属性
        return Result.success();
    }
    @RequestMapping("/integral/refund")
    Result<String> incrIntegral(@RequestBody OperateIntergralVo vo) {
        usableIntegralService.incrIntergralTry(vo, null);
//        usableIntegralMapper.incrIntergral(vo.getUserId(), vo.getValue());
        return Result.success();
    }
}

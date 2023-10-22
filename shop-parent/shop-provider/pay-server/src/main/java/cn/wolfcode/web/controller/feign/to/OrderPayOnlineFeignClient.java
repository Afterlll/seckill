package cn.wolfcode.web.controller.feign.to;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.config.AlipayProperties;
import cn.wolfcode.domain.PayVo;
import cn.wolfcode.domain.RefundVo;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/alipay")
public class OrderPayOnlineFeignClient {

    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private AlipayProperties alipayProperties;

    @RequestMapping("/online")
    Result<String> payOnline(@RequestBody PayVo payVo) throws AlipayApiException {
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(payVo.getReturnUrl()); // 同步回调
        alipayRequest.setNotifyUrl(payVo.getNotifyUrl()); // 异步回调

        // 返回支付的json
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ payVo.getOutTradeNo() +"\","
                + "\"total_amount\":\""+ payVo.getTotalAmount() +"\","
                + "\"subject\":\""+ payVo.getSubject() +"\","
                + "\"body\":\""+ payVo.getBody() +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        return Result.success(result);
    }

    // 验签
    @RequestMapping("/checkV1")
    Result<Boolean> checkV1(@RequestParam Map<String, String> map) throws AlipayApiException {
        boolean result = AlipaySignature.rsaCheckV1(
                map,
                alipayProperties.getAlipayPublicKey(),
                alipayProperties.getCharset(),
                alipayProperties.getSignType()
        );
        return Result.success(result);
    }

    // 在线退款
    @RequestMapping("/refundOnline")
    Result<Boolean> refundOnline(@RequestBody RefundVo refundVo) throws AlipayApiException {
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ refundVo.getOutTradeNo() +"\","
                + "\"trade_no\":\"\","
                + "\"refund_amount\":\""+ refundVo.getRefundAmount() +"\","
                + "\"refund_reason\":\""+ refundVo.getRefundReason() +"\","
                + "\"out_request_no\":\"\"}");
        AlipayTradeRefundResponse response = alipayClient.execute(alipayRequest);
        // 返回是否退款成功的标志 - response.isSuccess()
        return Result.success(response.isSuccess());
    }

}

package cn.wolfcode.web.controller;


import cn.wolfcode.common.web.CommonCodeMsg;
import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.OrderInfo;
import cn.wolfcode.domain.PayVo;
import cn.wolfcode.service.IOrderInfoService;
import cn.wolfcode.web.feign.OrderPayOnlineFeignApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

/**
 * Created by lanxw
 */
@RestController
@RequestMapping("/orderPay")
@RefreshScope // 识别nacos配置动态刷新
public class OrderPayController {
    @Autowired
    private IOrderInfoService orderInfoService;
    @Resource
    private OrderPayOnlineFeignApi orderPayOnlineFeignApi;
    // seckill/orderPay/pay?orderNo=1715170612558692352&type=0
    // 付款
    @GetMapping("/pay")
    public Result<String> orderPay(@RequestParam("orderNo") String orderNo, @RequestParam("type") String type) {
        if ("0".equals(type)) { // 在线支付
            return orderInfoService.payOrderOnline(orderNo);
        } else if("1".equals(type)) { // 积分支付
            return orderInfoService.payOrderIntegral(orderNo);
        }
        return Result.error(CommonCodeMsg.ILLEGAL_OPERATION);
    }
    // 退款
    // http://localhost:9000/seckill/orderPay/refund?orderNo=1714984444693053440
    @GetMapping("/refund")
    public Result<String> orderRefund(@RequestParam("orderNo") String orderNo) {
        OrderInfo orderInfo = orderInfoService.findByOrderNo(Long.parseLong(orderNo));
        if (OrderInfo.PAYTYPE_ONLINE.equals(orderInfo.getPayType())) {
            // 在线支付
            orderInfoService.refundOnline(orderInfo);
        } else if (OrderInfo.PAYTYPE_INTERGRAL.equals(orderInfo.getPayType())) {
            // 积分支付
            orderInfoService.refundIntegral(orderInfo);
        }
        return Result.success();
    }
    @Value("${pay.errorUrl}")
    private String errorUrl;
    @Value("${pay.frontEndPayUrl}")
    private String frontEndPayUrl;
    // 同步回调(跳转页面)
    @RequestMapping("/returnUrl")
    public void returnOrderUrl(@RequestParam Map<String, String> map, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 验签
        Result<Boolean> result = orderPayOnlineFeignApi.checkV1(map);
        if (result == null || result.hasError() || !result.getData()) {
            // 跳转错误页面
            response.sendRedirect(errorUrl);
            return;
        }
        // 跳转支付成功下一步页面(订单详情页面->订单状态是否已经更改完毕)
        response.sendRedirect(frontEndPayUrl + request.getParameter("out_trade_no"));
    }
    // 异步回调
    @RequestMapping("/notifyUrl")
    public String notifyOrderUrl(@RequestParam Map<String, String> map, HttpServletRequest request) throws UnsupportedEncodingException {
        // 验签
        Result<Boolean> result = orderPayOnlineFeignApi.checkV1(map);
        if (result == null || result.hasError()) {
            return "failed";
        }
        if (result.getData()) {
            // 验签成功
            // 修改订单的状态
            //商户订单号
            String orderNo = request.getParameter("out_trade_no");
            int effectCount = orderInfoService.changePayStatus(orderNo, OrderInfo.STATUS_ACCOUNT_PAID, OrderInfo.PAYTYPE_ONLINE);
            if (effectCount == 0) { // 状态机失败（订单状态已经不是未支付了）
                // TODO 执行退款逻辑
//                orderInfoService.refundOnline(orderInfoService.findByOrderNo(Long.parseLong(orderNo)));
            }
        }
        // 验签失败也是返回”success“，此时可能是用户恶意修改了参数，不需要继续回调逻辑了
        return "success"; // 异步回调返回“success“就不需要继续回调该逻辑了
    }
}

package cn.wolfcode.service;


import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.OrderInfo;
import cn.wolfcode.domain.SeckillProductVo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * Created by wolfcode-lanxw
 */
public interface IOrderInfoService {

    OrderInfo findByPhoneAndSeckillId(String phone, Long seckillId);

    OrderInfo doSeckill(String phone, SeckillProductVo seckillProductVo);

    OrderInfo findByOrderNo(Long orderNo);

    void cancleOrder(String orderNo);

    Result<String> payOrderOnline(String orderNo);

    Result<String> payOrderIntegral(String orderNo);
    int changePayStatus( String orderNo,  Integer status,  int payType);

    void refundOnline(OrderInfo orderInfo);

    Result<String> refundIntegral(OrderInfo orderInfo);

}

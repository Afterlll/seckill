package cn.wolfcode.mq;

import lombok.*;

import java.io.Serializable;

/**
 * Created by wolfcode-lanxw
 * 封装异步下单的参数
 */
@Setter
@Getter
@Data
public class OrderMessage implements Serializable {
    private Integer time;//秒杀场次
    private Long seckillId;//秒杀商品ID
    private String token;//用户的token信息
    private Long userPhone;//用户手机号码

    public OrderMessage() {
    }

    public OrderMessage(Integer time, Long seckillId, String token, Long userPhone) {
        this.time = time;
        this.seckillId = seckillId;
        this.token = token;
        this.userPhone = userPhone;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(Long userPhone) {
        this.userPhone = userPhone;
    }
}

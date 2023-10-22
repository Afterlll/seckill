package cn.wolfcode.mq;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by lanxw
 */
@Data
public class OrderMQResult implements Serializable {
    private Integer time;
    private Long seckillId;
    private String orderNo;
    private String msg;
    private Integer code;
    private String token;
}

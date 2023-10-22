package cn.wolfcode.common.web.interceptor;

import cn.wolfcode.common.constants.CommonConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Created by lanxw
 * Feign调用时添加标记为此时是通过Feign进行调用（不需要登录）
 */
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header(CommonConstants.FEIGN_REQUEST_KEY,CommonConstants.FEIGN_REQUEST_TRUE);
    }
}

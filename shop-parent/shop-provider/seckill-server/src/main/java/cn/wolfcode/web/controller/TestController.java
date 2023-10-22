package cn.wolfcode.web.controller;

import cn.wolfcode.common.constants.CommonConstants;
import cn.wolfcode.common.web.anno.RequireLogin;
import cn.wolfcode.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequireLogin
    @RequestMapping("/test")
    public String test(HttpServletRequest request) {
        System.out.println("测试方法");
        String token = request.getHeader(CommonConstants.TOKEN_NAME);
        System.out.println(UserUtil.getUserPhone(redisTemplate, token));
        return "test";
    }

}

package cn.wolfcode.controller;

import cn.wolfcode.ws.OrderWebSocketServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.Session;
import java.io.IOException;

@RestController
public class OrderWebSocketController {
    @RequestMapping("/sendMessage")
    public String sendMessage(@RequestParam("token") String token, @RequestParam("msg") String msg) throws IOException {
        Session session = OrderWebSocketServer.clients.get(token);
        session.getBasicRemote().sendText(msg); // 发送给浏览器
        return "发送成功";
    }
}
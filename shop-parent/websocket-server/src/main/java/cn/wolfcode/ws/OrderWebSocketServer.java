package cn.wolfcode.ws;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

@Data
@ServerEndpoint("/{token}")
@Component
public class OrderWebSocketServer {
    private Session session;
    public static ConcurrentHashMap<String,Session> clients = new ConcurrentHashMap<>();
    @OnOpen
    public void onOpen(Session session, @PathParam( "token") String token){
        System.out.println("客户端连接===>"+token);
        clients.put(token, session);
    }
    // 接收浏览器发送来的数据
    @OnMessage
    public void onMessage(@PathParam( "token") String token, String msg) {
        System.out.println("浏览器 " + token + " 发送来的信息：" + msg);
    }
    @OnClose
    public void onClose(@PathParam( "token") String token){
        clients.remove(token);
    }
    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }
}
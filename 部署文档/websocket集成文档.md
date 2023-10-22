# websocket集成文档

1.添加依赖

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

2.添加配置

```java
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```

3.添加websocket处理器

```java
@Setter@Getter
@ServerEndpoint("/{token}")
@Component
public class WebSocketServer {
    private Session session;
    public static ConcurrentHashMap<String,Session> clients = new ConcurrentHashMap<>();
    @OnOpen
    public void onOpen(Session session, @PathParam( "token") String token){
        System.out.println("客户端连接===>"+token);
        clients.put(token,session);
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
```


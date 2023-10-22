# canal客户端集成文档

1.首先启动Canal Server，具体部署参考给的文档

2.添加依赖

```xml
<dependency>
	<groupId>top.javatool</groupId>
	<artifactId>canal-spring-boot-starter</artifactId>
	<version>1.2.1-RELEASE</version>
</dependency>
```

3.添加配置如下

```yaml
canal:
  server: Canal服务部署的地址:11111
  destination: example
logging:
  level:
    root: info
    top:
      javatool:
        canal:
          client:
            client:
              AbstractCanalClient: error
```

4.添加Handler

```java
@Slf4j
@Component
@CanalTable(value = "t_order_info")
public class OrderaInfoHandler implements EntryHandler<OrderInfo> {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public void insert( OrderInfo orderInfo) {
        log.info("当有数据插入的时候会触发这个方法);
    }

    @Override
    public void update(OrderInfo before, OrderInfo after) {
        log.info("当有数据更新的时候会触发这个方法);
    }

    @Override
    public void delete(OrderInfo orderInfo) {
        log.info("当有数据删除的时候会触发这个方法);
    }
}
```


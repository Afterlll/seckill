# Seata客户端集成文档

1.启动seata-server,详情请看部署文档

2.在项目中添加依赖

```xml
 <dependency>
	<groupId>com.alibaba.cloud</groupId>
	<artifactId>spring-cloud-starter-alibaba-seata</artifactId>
	<version>2.2.2.RELEASE</version>
	<exclusions>
		<exclusion>
            <groupId>io.seata</groupId>
			<artifactId>seata-spring-boot-starter</artifactId>
		</exclusion>
	</exclusions>
</dependency>
<dependency>
	<groupId>io.seata</groupId>
	<artifactId>seata-spring-boot-starter</artifactId>
	<version>1.3.0</version>
</dependency>
```

在配置文件中添加如下配置

```yaml
seata:
  tx-service-group: seckill-service-group
  registry:
    type: nacos
    nacos:
      server-addr: ${spring.cloud.nacos.discovery.server-addr}
      group: SEATA_GROUP
      application: seata-server
  service:
    vgroup-mapping:
      seckill-service-group: default
```


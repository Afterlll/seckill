# seckill

#### 介绍
可插拔式秒杀/限时抢购系统

1. 微服务架构下的高并发使用场景（seata-AT、seata-TCC、全局事务、mq事务消息）
2. 限时商品上架（elastic-job）
3. 高并发环境下的优化手段（redis缓存、mq、限流）
4. 支付环境：支付宝沙箱、积分支付、退款、退积分
5. 数据库与redis缓存的一致性（canal）
6. 异步下单（websocket监测下单结果，延迟消息队列）

#### 核心技术栈
springcloudalibaba、redis、rocketmq、canal、wobsocket、elastic-job、zookeeper

#### 项目截图

#### 代码结构
```
+---api-gateway
|   |   api-gateway.iml
|   |   pom.xml
|   |
|   +---src
|   |   \---main
|   |       +---java
|   |       |   \---cn
|   |       |       \---wolfcode
|   |       |           |   ApiGatewayApplication.java
|   |       |           |
|   |       |           +---config
|   |       |           |       CorsConfig.java
|   |       |           |       GatewayConfiguration.java
|   |       |           |
|   |       |           \---filters
|   |       |                   CommonFilter.java
|   |       |
|   |       \---resources
|   |               bootstrap.yml
|   |
+---canal-client
|   |   canal-client.iml
|   |   pom.xml
|   |
|   +---src
|   |   \---main
|   |       +---java
|   |       |   \---cn
|   |       |       \---wolfcode
|   |       |           |   CanalClientApp.java
|   |       |           |
|   |       |           \---handler
|   |       |                   OrderaInfoHandler.java
|   |       |
|   |       \---resources
|   |               bootstrap.yml
|   |
+---shop-common
|   |   pom.xml
|   |
|   +---src
|   |   \---main
|   |       \---java
|   |           \---cn
|   |               \---wolfcode
|   |                   +---common
|   |                   |   +---constants
|   |                   |   |       CommonConstants.java
|   |                   |   |
|   |                   |   +---domain
|   |                   |   |       UserInfo.java
|   |                   |   |
|   |                   |   +---exception
|   |                   |   |       BusinessException.java
|   |                   |   |       CommonControllerAdvice.java
|   |                   |   |
|   |                   |   \---web
|   |                   |       |   CodeMsg.java
|   |                   |       |   CommonCodeMsg.java
|   |                   |       |   Result.java
|   |                   |       |
|   |                   |       +---anno
|   |                   |       |       RequireLogin.java
|   |                   |       |
|   |                   |       \---interceptor
|   |                   |               FeignRequestInterceptor.java
|   |                   |               RequireLoginInterceptor.java
|   |                   |
|   |                   \---redis
|   |                           CommonRedisKey.java
|   |
+---shop-provider
|   |   pom.xml
|   |
|   +---intergral-server
|   |   |   intergral-server.iml
|   |   |   pom.xml
|   |   |
|   |   +---src
|   |   |   \---main
|   |   |       +---java
|   |   |       |   \---cn
|   |   |       |       \---wolfcode
|   |   |       |           |   IntergralApplication.java
|   |   |       |           |
|   |   |       |           +---mapper
|   |   |       |           |       AccountTransactionMapper.java
|   |   |       |           |       AccountTransactionMapper.xml
|   |   |       |           |       UsableIntegralMapper.java
|   |   |       |           |       UsableIntegralMapper.xml
|   |   |       |           |
|   |   |       |           +---service
|   |   |       |           |   |   IUsableIntegralService.java
|   |   |       |           |   |
|   |   |       |           |   \---impl
|   |   |       |           |           UsableIntegralServiceImpl.java
|   |   |       |           |
|   |   |       |           \---web
|   |   |       |               +---advice
|   |   |       |               |       IntergralControllerAdvice.java
|   |   |       |               |
|   |   |       |               +---config
|   |   |       |               |       WebConfig.java
|   |   |       |               |
|   |   |       |               +---controller
|   |   |       |               |       IntegralController.java
|   |   |       |               |
|   |   |       |               +---feign
|   |   |       |               |   \---to
|   |   |       |               |           OrderPayIntegralFeignClient.java
|   |   |       |               |
|   |   |       |               \---msg
|   |   |       |                       IntergralCodeMsg.java
|   |   |       |
|   |   |       \---resources
|   |   |               bootstrap.yml
|   |   |
|   +---job-server
|   |   |   job-server.iml
|   |   |   pom.xml
|   |   |
|   |   +---src
|   |   |   \---main
|   |   |       +---java
|   |   |       |   \---cn
|   |   |       |       \---wolfcode
|   |   |       |           |   JobApplication.java
|   |   |       |           |
|   |   |       |           +---config
|   |   |       |           |       BusinessJobConfig.java
|   |   |       |           |       RegistryCenterConfig.java
|   |   |       |           |
|   |   |       |           +---feign
|   |   |       |           |   |   SeckillProductFeignApi.java
|   |   |       |           |   |
|   |   |       |           |   \---fallback
|   |   |       |           |           SeckillProductFallback.java
|   |   |       |           |
|   |   |       |           +---job
|   |   |       |           |       SeckillProductJob.java
|   |   |       |           |       UserCacheJob.java
|   |   |       |           |
|   |   |       |           +---redis
|   |   |       |           |       JobRedisKey.java
|   |   |       |           |
|   |   |       |           +---util
|   |   |       |           |       ElasticJobUtil.java
|   |   |       |           |
|   |   |       |           \---web
|   |   |       |               \---config
|   |   |       |                       WebConfig.java
|   |   |       |
|   |   |       \---resources
|   |   |               bootstrap.yml
|   |   |
|   +---pay-server
|   |   |   pay-server.iml
|   |   |   pom.xml
|   |   |
|   |   +---src
|   |   |   \---main
|   |   |       +---java
|   |   |       |   \---cn
|   |   |       |       \---wolfcode
|   |   |       |           |   PayApplication.java
|   |   |       |           |
|   |   |       |           +---config
|   |   |       |           |       AlipayConfig.java
|   |   |       |           |       AlipayProperties.java
|   |   |       |           |
|   |   |       |           \---web
|   |   |       |               +---advice
|   |   |       |               |       PayControllerAdvice.java
|   |   |       |               |
|   |   |       |               +---config
|   |   |       |               |       WebConfig.java
|   |   |       |               |
|   |   |       |               +---controller
|   |   |       |               |   |   AlipayController.java
|   |   |       |               |   |
|   |   |       |               |   \---feign
|   |   |       |               |       \---to
|   |   |       |               |               OrderPayOnlineFeignClient.java
|   |   |       |               |
|   |   |       |               \---msg
|   |   |       |                       PayCodeMsg.java
|   |   |       |
|   |   |       \---resources
|   |   |               bootstrap.yml
|   |   |
|   +---product-server
|   |   |   pom.xml
|   |   |   product-server.iml
|   |   |
|   |   +---src
|   |   |   \---main
|   |   |       +---java
|   |   |       |   \---cn
|   |   |       |       \---wolfcode
|   |   |       |           |   ProductApplication.java
|   |   |       |           |
|   |   |       |           +---mapper
|   |   |       |           |       ProductMapper.java
|   |   |       |           |       ProductMapper.xml
|   |   |       |           |
|   |   |       |           +---service
|   |   |       |           |   |   IProductService.java
|   |   |       |           |   |
|   |   |       |           |   \---impl
|   |   |       |           |           ProductServiceImpl.java
|   |   |       |           |
|   |   |       |           \---web
|   |   |       |               +---advice
|   |   |       |               |       ProductControllerAdvice.java
|   |   |       |               |
|   |   |       |               +---config
|   |   |       |               |       WebConfig.java
|   |   |       |               |
|   |   |       |               +---controller
|   |   |       |               |       ProductController.java
|   |   |       |               |
|   |   |       |               +---feign
|   |   |       |               |       ProductFeignClient.java
|   |   |       |               |
|   |   |       |               \---msg
|   |   |       |                       ProductCodeMsg.java
|   |   |       |
|   |   |       \---resources
|   |   |               bootstrap.yml
|   |   |
|   \---seckill-server
|       |   pom.xml
|       |   seckill-server.iml
|       |
|       +---src
|       |   \---main
|       |       +---java
|       |       |   \---cn
|       |       |       \---wolfcode
|       |       |           |   SeckillApplication.java
|       |       |           |
|       |       |           +---mapper
|       |       |           |       OrderInfoMapper.java
|       |       |           |       OrderInfoMapper.xml
|       |       |           |       PayLogMapper.java
|       |       |           |       PayLogMapper.xml
|       |       |           |       RefundLogMapper.java
|       |       |           |       RefundLogMapper.xml
|       |       |           |       SeckillProductMapper.java
|       |       |           |       SeckillProductMapper.xml
|       |       |           |
|       |       |           +---mq
|       |       |           |       MQConstant.java
|       |       |           |       OrderMessage.java
|       |       |           |       OrderMQResult.java
|       |       |           |       OrderPeddingQueueListener.java
|       |       |           |       OrderPeddingTimeoutListener.java
|       |       |           |       OrderResultFailedListener.java
|       |       |           |
|       |       |           +---service
|       |       |           |   |   IOrderInfoService.java
|       |       |           |   |   ISeckillProductService.java
|       |       |           |   |
|       |       |           |   \---impl
|       |       |           |           OrderInfoSeviceImpl.java
|       |       |           |           SeckillProductServiceImpl.java
|       |       |           |
|       |       |           +---util
|       |       |           |       DateUtil.java
|       |       |           |       IdGenerateUtil.java
|       |       |           |       UserUtil.java
|       |       |           |
|       |       |           \---web
|       |       |               +---advice
|       |       |               |       SeckillControllerAdvice.java
|       |       |               |
|       |       |               +---config
|       |       |               |       WebConfig.java
|       |       |               |
|       |       |               +---controller
|       |       |               |       OrderInfoController.java
|       |       |               |       OrderPayController.java
|       |       |               |       SeckillProductController.java
|       |       |               |       TestController.java
|       |       |               |
|       |       |               +---feign
|       |       |               |   |   OrderPayIntegralFeignApi.java
|       |       |               |   |   OrderPayOnlineFeignApi.java
|       |       |               |   |   ProductFeignApi.java
|       |       |               |   |
|       |       |               |   +---fallback
|       |       |               |   |       OrderPayIntegralFallback.java
|       |       |               |   |       OrderPayOnlineFallback.java
|       |       |               |   |       ProductFenApiFallback.java
|       |       |               |   |
|       |       |               |   \---to
|       |       |               |           SeckillProductClient.java
|       |       |               |
|       |       |               \---msg
|       |       |                       SeckillCodeMsg.java
|       |       |
|       |       \---resources
|       |               bootstrap.yml
|       |
+---shop-provider-api
|   |   pom.xml
|   |
|   +---intergral-api
|   |   |   pom.xml
|   |   |
|   |   +---src
|   |   |   \---main
|   |   |       \---java
|   |   |           \---cn
|   |   |               \---wolfcode
|   |   |                   \---domain
|   |   |                           AccountTransaction.java
|   |   |                           OperateIntergralVo.java
|   |   |                           UsableIntegral.java
|   |   |
|   |   \---target
|   |       +---classes
|   |       |   \---cn
|   |       |       \---wolfcode
|   |       |           \---domain
|   |       |                   AccountTransaction.class
|   |       |                   OperateIntergralVo.class
|   |       |                   UsableIntegral.class
|   |       |
|   |       \---generated-sources
|   |           \---annotations
|   +---pay-api
|   |   |   pom.xml
|   |   |
|   |   +---src
|   |   |   \---main
|   |   |       \---java
|   |   |           \---cn
|   |   |               \---wolfcode
|   |   |                   \---domain
|   |   |                           PayVo.java
|   |   |                           RefundVo.java
|   |   |
|   +---product-api
|   |   |   pom.xml
|   |   |
|   |   +---src
|   |   |   \---main
|   |   |       \---java
|   |   |           \---cn
|   |   |               \---wolfcode
|   |   |                   \---domain
|   |   |                           Product.java
|   |   |
|   \---seckill-api
|       |   pom.xml
|       |
|       +---src
|       |   \---main
|       |       \---java
|       |           \---cn
|       |               \---wolfcode
|       |                   +---domain
|       |                   |       OrderInfo.java
|       |                   |       PayLog.java
|       |                   |       RefundLog.java
|       |                   |       SeckillProduct.java
|       |                   |       SeckillProductVo.java
|       |                   |
|       |                   \---redis
|       |                           SeckillRedisKey.java
|       |
+---shop-uaa
|   |   pom.xml
|   |   shop-uaa.iml
|   |
|   +---src
|   |   \---main
|   |       +---java
|   |       |   \---cn
|   |       |       \---wolfcode
|   |       |           |   UaaApplication.java
|   |       |           |
|   |       |           +---domain
|   |       |           |       LoginLog.java
|   |       |           |       UserLogin.java
|   |       |           |       UserResponse.java
|   |       |           |
|   |       |           +---mapper
|   |       |           |       UserMapper.java
|   |       |           |       UserMapper.xml
|   |       |           |
|   |       |           +---mq
|   |       |           |       MQConstant.java
|   |       |           |       MQLoginLogListener.java
|   |       |           |
|   |       |           +---redis
|   |       |           |       UaaRedisKey.java
|   |       |           |
|   |       |           +---service
|   |       |           |   |   IUserService.java
|   |       |           |   |
|   |       |           |   \---impl
|   |       |           |           UserServiceImpl.java
|   |       |           |
|   |       |           +---util
|   |       |           |       MD5Util.java
|   |       |           |
|   |       |           \---web
|   |       |               +---advice
|   |       |               |       UAAControllerAdvice.java
|   |       |               |
|   |       |               +---config
|   |       |               |       WebConfig.java
|   |       |               |
|   |       |               +---controller
|   |       |               |       LoginController.java
|   |       |               |       TokenController.java
|   |       |               |
|   |       |               \---msg
|   |       |                       UAACodeMsg.java
|   |       |
|   |       \---resources
|   |               bootstrap.yml
|   |
+---websocket-server
|   |   pom.xml
|   |   websocket-server.iml
|   |
|   +---src
|   |   \---main
|   |       +---java
|   |       |   \---cn
|   |       |       \---wolfcode
|   |       |           |   WebsocketServerApplication.java
|   |       |           |
|   |       |           +---config
|   |       |           |       WebSocketConfig.java
|   |       |           |
|   |       |           +---controller
|   |       |           |       OrderWebSocketController.java
|   |       |           |
|   |       |           +---mq
|   |       |           |       MQConstants.java
|   |       |           |       OrderMQResult.java
|   |       |           |       OrderResultQueueListener.java
|   |       |           |
|   |       |           \---ws
|   |       |                   OrderWebSocketServer.java
|   |       |
|   |       \---resources
|   |               bootstrap.yml
|   |
\---配置文件
    +---nacos配置
    |       nacos_config_export.zip
    |
    \---SQL脚本
            shop-intergral.sql
            shop-product.sql
            shop-seckill.sql
            shop-uaa.sql
```

#### 软件架构
软件架构说明
![输入图片说明](https://foruda.gitee.com/images/1697940817700003522/8fcfc6ec_11575753.png "系统架构图.png")
![输入图片说明](https://foruda.gitee.com/images/1697940680216441448/fdbfabf6_11575753.png "限时抢购的流程-互联网商户.png")

#### 安装教程

1.  导入nacos的配置，启动nacos，修改开发环境的配置，配置环境在/shop-parent/配置文件
2.  导入数据库脚本，位置在/shop-parent/配置文件
3.  部署各类开发环境，可参考部署文档（mysql、canal、rocketmq、seata、redis、zookeeper、nacos...）

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)

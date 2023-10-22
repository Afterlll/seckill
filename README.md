# seckill

#### 介绍
可插拔式秒杀/限时抢购系统

1. 微服务架构下的高并发使用场景（seata-AT、seata-TCC、全局事务、mq事务消息）
2. 限时商品上架（elastic-job）
3. 高并发环境下的优化手段（redis缓存、mq、限流）
4. 集成支付宝沙箱环境
5. 数据库与redis缓存的一致性（canal）
6. 异步下单（websocket监测下单结果）

#### 核心技术栈
springcloudalibaba、redis、rocketmq、canal、wobsocket、elastic-job、zookeeper

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

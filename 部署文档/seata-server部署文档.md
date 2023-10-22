# seata-server部署文档

1.上传，将`seata-server-1.3.0.zip`上传到`/usr/local/software`目录下

2.解压文件到指定目录

```shell
unzip /usr/local/software/seata-server-1.3.0.zip -d /usr/local
```

3.修改日志配置文件，否则启动控制台乱码(如果是window的情况需要修改如下配置)

```shell
vi /usr/local/seata/conf/logback.xml
```

原配置如下:

```xml
<property name="CONSOLE_LOG_PATTERN" value="%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wEx"/>
```

修改成如下格式:

```xml
<property name="CONSOLE_LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %5p --- %[%15.15t] %-40.40logger{39} : %m%n%wEx"/>
```

此问题是因为开发者为seata1.3.0添加字体颜色，而在window中的shell脚本内不显示发生的乱码错误

4.修改registry.config文件

```shell
vi /usr/local/seata/conf/registry.conf
```

修改内容如下:[注意需要把下面`nacos的IP地址`修改成实际地址]

```properties
registry {
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  type = "nacos"

  nacos {
    application = "seata-server"
    serverAddr = "nacos的IP地址:8848"
    group = "SEATA_GROUP"
    namespace = ""
    cluster = "default"
    username = ""
    password = ""
  }
}

config {
  # file、nacos 、apollo、zk、consul、etcd3
  type = "nacos"
  nacos {
    serverAddr = "nacos的IP地址:8848"
    namespace = ""
    group = "SEATA_GROUP"
    username = ""
    password = ""
  }
}

```

5.启动seata-server

```shell
nohup /usr/local/seata/bin/seata-server.sh -h 目前所在服务器ip地址 -p 7000 >log.out 2>1 &
```


# Zookeeper部署文档

1.上传,将`zookeeper-3.4.11.tar.gz`上传到`/usr/local/software`目录下

2.解压文件到指定目录

```shell
tar -zxvf /usr/local/software/zookeeper-3.4.11.tar.gz -C /usr/local/
```

3.拷贝配置文件

```shell
cp /usr/local/software/zookeeper-3.4.11/conf/zoo_sample.cfg /usr/local/software/zookeeper-3.4.11/conf/zoo.cfg
```

4.启动

```shell
/usr/local/zookeeper-3.4.11/bin/zkServer.sh start
```

5.检查进程是否开启

```
jps
```

需要查看到`QuorumPeerMain`进程


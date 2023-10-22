# nacos部署文档

1.上传文件,将`nacos-server-1.3.2.zip`上传到`/usr/local/software`

2.解压文件到指定目录

```shell
unzip /usr/local/software/nacos-server-1.3.2.zip -d /usr/local/
```

3.启动nacos

```shell
/usr/local/nacos/bin/startup.sh -m standalone
```

4.检查是否启动成功

```shell
jps
```

需要查看到有`nacos-server.jar`进程


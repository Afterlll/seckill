### RocketMQ安装步骤

1.将压缩包上传服务器,把`rocketmq-all-4.4.0-bin-release.zip`拷贝到`/usr/local/software`

2.使用解压命令进行解压到`/usr/local`目录

```shell
unzip /usr/local/software/rocketmq-all-4.4.0-bin-release.zip -d /usr/local
```

3.软件文件名重命名

```shell
mv  /usr/local/rocketmq-all-4.4.0-bin-release/  /usr/local/rocketmq-4.4/
```

4.设置环境变量

```shell
sudo vim ~/.bashrc
export JAVA_HOME=/opt/java/jdk1.8.0_161
export ROCKETMQ_HOME=/opt/rocketmq-4.4
export PATH=$ROCKETMQ_HOME/bin:$PATH
source ~/.bashrc
```

5.修改脚本中的JVM相关参数,修改文件如下

```shell
vi  /usr/local/rocketmq-4.4/bin/runbroker.sh
vi  /usr/local/rocketmq-4.4/bin/runserver.sh
```

修改启动参数配置

```shell
JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
```

6.修改配置文件

```shell
 vi /usr/local/rocketmq-4.4/conf/broker.conf
```

新增配置如下:

**![image-20201209150706832](图片/image-20201209150753513.png)**

```
namesrvAddr=192.168.174.128:9876
brokerIP1=192.168.147.128
```



7.启动NameServer

```shell
# 1.启动NameServer
nohup ./mqnamesrv &
# 2.查看启动日志
tail -f ~/logs/rocketmqlogs/namesrv.log
```

8.启动Broker

```shell
#1.启动Broker
nohup ./mqbroker -n 192.168.174.128:9876 -c /opt/rocketmq-4.4/conf/broker.conf &
#2.查看启动日志
tail -f ~/logs/rocketmqlogs/broker.log
```

9.使用命令查看是否开启成功

```shell
jps
```

需要看到`NamesrvStartup`和`BrokerStartup`这两个进程

10. 设置开机自启

    1. ```
       vim /lib/systemd/system/rc-local.service
       
       #### 文件中本身就有的
       [Unit]
       Description=/etc/rc.local Compatibility
       Documentation=man:systemd-rc-local-generator(8)
       ConditionFileIsExecutable=/etc/rc.local
       After=network.target
       
       [Service]
       Type=forking
       ExecStart=/etc/rc.local start
       TimeoutSec=0
       RemainAfterExit=yes
       GuessMainPID=no
       
       ####  需要自己添加
       [Install]
       WantedBy=multi-user.target
       Alias=rc-local.service
       
       ```

    2. ```
       touch /etc/rc.local
       
       #! /bin/bash
       #### 这里在/usr/local里面创建文件夹是想看是否有执行的权限
       #### 事实证明是有的
       nohup ./mqnamesrv &
       nohup ./mqbroker -n 192.168.174.128:9876 -c /opt/rocketmq-4.4/conf/broker.conf &
       ```

    3. ```
       sudo chmod +x /etc/rc.local
       ```

    4. ```
       sudo systemctl enable rc-local
       ```

    5. ```
       sudo systemctl start rc-local.service
       sudo systemctl status rc-local.service
       ```

    6. 

    


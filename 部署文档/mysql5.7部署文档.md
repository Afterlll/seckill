# 安装mysql5.7

1、先把postfix 和mariadb-libs卸载掉，不然的会有依赖包冲突：

```shell
rpm -e postfix mariadb-libs
```

2、安装mysql的依赖net-tools和 perl

```shell
yum -y install net-tools perl
```

3、安装mysql-common包：

```shell
rpm -ivh mysql-community-common-5.7.22-1.el7.x86_64.rpm
```

4、安装mysql-libs包：

```shell
rpm -ivh mysql-community-libs-5.7.22-1.el7.x86_64.rpm
```

5、安装mysql-client包；

```shell
rpm -ivh mysql-community-client-5.7.22-1.el7.x86_64.rpm
```

6、安装mysql-server包

```shell
rpm -ivh mysql-community-server-5.7.22-1.el7.x86_64.rpm
```

7、设置开机启动：

```shell
systemctl enable mysqld
```

8、启动MySql服务

```shell
systemctl start mysqld
```

9、由于MySQL5.7安装好后会给root用户分配一个临时密码，所以我们先查看临时密码

```shell
grep 'temporary password' /var/log/mysqld.log
```

**2018-06-01T19:40:08.341478Z 1 [Note] A temporary password is generated for root@localhost: Ct<pX.k7S(=w**

冒号后面的就是root用户的临时密码：**Ct<pX.k7S(=w**

10、使用临时密码登录

```shell
mysql -u root -p
```

**输入密码：Ct<pX.k7S(=w**

11、设置root的密码

```mysql
mysql>ALTER USER 'root'@'localhost' IDENTIFIED BY 'WolfCode_2017';
```

***\*注意：mysql5.7增加了安全级别，密码\*******\*必须包含：大小写字母、数字和特殊符号，并且长度不能少于8位。\****

11、开放远程登录权限

```mysql
mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'WolfCode_2017'  WITH GRANT OPTION;
mysql> FLUSH PRIVILEGES;
```

12、开放mysql的3306端口

```shell
firewall-cmd --zone=public --add-port=3306/tcp --permanent
firewall-cmd --reload
```

如果出现乱码:

在链接地址栏后添加`useUnicode=true&characterEncoding=utf-8`

 

 
# 基于Canal、MQ刷新Redis缓存

[Cannel QuickStart](https://github.com/alibaba/canal/wiki/QuickStart)

Canal特点：

+ 支持所有平台

+ 支持由Prometheus提供支持的细粒系统监测

+ 支持通过不同的方式解析和订阅MySQL Binlog，例如由GTID

+ 支持高性能，实时数据同步。（请参阅更多表演）

+ Canal服务器和Canal客户端都支持HA/可伸缩性，由Apache Zookeeper提供支持

  > 即高可用部署Canal需要安装Zookeeper。

+ 支持Docker

组件：

+ canal-server
+ canal-client (已经封装好了一些常用客户端，如：MQ、HBase、ES、RDB)
+ zookeeper (高可用时需要)
+ canal-admin (非必须, SpringBoot实现的一个后端管理服务)

## 基本配置

### MySQL

主要是开启binlog ROW模式。

我的MySQL配置文件路径`/etc/mysql/my.conf.d/lee.cnf`

```
[mysqld]
log-bin=mysql-bin # 开启 binlog
binlog-format=ROW # 选择 ROW 模式
server_id=1 # 配置 MySQL replaction 需要定义，不要和 canal 的 slaveId 重复
```

重启MySQL  `service mysql restart` ，查看binlog是否开启以及日志位置 `show variables like 'log_%'`

```
+----------------------------------------+--------------------------------+
| Variable_name                          | Value                          |
+----------------------------------------+--------------------------------+
| log_bin                                | ON                             |
| log_bin_basename                       | /var/lib/mysql/mysql-bin       |
| log_bin_index                          | /var/lib/mysql/mysql-bin.index |
```

授权 canal 链接 MySQL 账号具有作为 MySQL slave 的权限

```sql
CREATE USER canal IDENTIFIED BY 'canal';  
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
FLUSH PRIVILEGES;
-- 查看新建的用户
select * from mysql.user where User = 'canal';
```

### Canal

也支持Docker部署，但是官方的镜像把Linux内核包进去了太大了

```shell
tar zxvf canal.deployer-1.1.6.tar.gz -C /opt/canal
```

按官网配置`conf/example/instance.properties`，然后启动Canal

主要是配置Canal用哪个MySQL用户连接哪个MySQL实例（Master, ip:port）、Canal作为Slave的slaveId。

```shell
sh bin/startup.sh
tail -f logs/canal/canal.log
```

### Java客户端测试

运行官方的Java客户端测试`SimpleCanalClientExample`，然后执行建表、插入，可以看到类似下面信息标识Canal运行正常

```
================> binlog[mysql-bin.000001:1448] , name[test,xdual] , eventType : INSERT
ID : 3    update=true
X : 2023-03-16 09:52:51    update=true
```

### RocketMQ

这里选择用RocketMQ，使用docker搭建可参考 kwseeker/message-queue dc-pre.sh, 之前写的一键部署shell脚本。

但是镜像有点旧了，这里用二进制包部署

```shell
wget https://dist.apache.org/repos/dist/release/rocketmq/5.1.0/rocketmq-all-5.1.0-bin-release.zip
# 启动NameServer
nohup sh bin/mqnamesrv &
tail -f ~/logs/rocketmqlogs/namesrv.log
# 启动Broker+Proxy
nohup sh bin/mqbroker -n 127.0.0.1:9876 --enable-proxy &
# 测试时硬盘空闲空间不足10%报异常，改下broker.conf
# diskMaxUsedSpaceRatio=99
tail -f ~/logs/rocketmqlogs/proxy.log 
```

> proxy 是 broker 的代理服务。

启动 [rokcetmq-dashboard](https://github.com/apache/rocketmq-dashboard/blob/master/docs/1_0_0/UserGuide_CN.md)

```shell
wget https://github.com/apache/rocketmq-dashboard/archive/refs/tags/rocketmq-dashboard-1.0.0.zip
mvn clean package -Dmaven.test.skip=true
# 需要改下端口、改下NameServerAddr
java -jar target/rocketmq-dashboard-1.0.0.jar --server.port=18081 --rocketmq.config.namesrvAddr=127.0.0.1:9876
```

RocketMQ 生产消费测试：RocketMQProducerTest

## 配置Canal将binlog投递到RocketMQ

修改Canal安装目录下的 `/conf/canal.properties`, 然后重启。

```properties
canal.serverMode = rocketMQ
# 
canal.destinations = example
# 配置mq的group地址、group、topic及tag
rocketmq.producer.group =GID_P_REFRESHER
#rocketmq.customized.trace.topic=refresher
#rocketmq.namespace=MQ_INST_10142odK
rocketmq.namesrv.addr=127.0.0.1:9876
rocketmq.tag=refresher
```

参考：[mq相关参数说明 (>=1.1.5版本)](https://github.com/alibaba/canal/wiki/Canal-Kafka-RocketMQ-QuickStart#mq%E7%9B%B8%E5%85%B3%E5%8F%82%E6%95%B0%E8%AF%B4%E6%98%8E-115%E7%89%88%E6%9C%AC)

## binlog日志消费＆刷新缓存


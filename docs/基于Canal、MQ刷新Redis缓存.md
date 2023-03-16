# 基于Canal、MQ刷新Redis缓存

[Cannel QuickStart](https://github.com/alibaba/canal/wiki/QuickStart)



## 基本配置

### MySQL

我的MySQL配置文件路径`/etc/mysql/my.conf.d/lee.cnf`

```
[mysqld]
log-bin=mysql-bin # 开启 binlog
binlog-format=ROW # 选择 ROW 模式
server_id=1 # 配置 MySQL replaction 需要定义，不要和 canal 的 slaveId 重复
```

重启MySQL，查看binlog是否开启以及日志位置 `show variables like 'log_%'`

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

```shell
sudo tar zxvf canal.deployer-1.1.6.tar.gz -C /opt/canal
```

按官网配置，然后启动Canal。

```shell
sh bin/startup.sh
tail -f logs/canal/canal.log
```



## Java客户端


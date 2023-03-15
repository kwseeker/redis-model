# Redis监测与性能优化



## 监控

可以使用 RedisInsight  或 Grafana 监控（安装Redis Data Source插件）。



## 性能优化

+ 规范设计key value

+ 防止 bigkey 出现

  如value超过10KB的String类型key、内部元素个数超过5000的非String类型key。

  bigkey可能导致查询等性能低下、进而可能导致Redis操作阻塞、网络阻塞。

  bigkey优化：

  + 将bigkey拆分成多个key存储
  + 如果bigkey不可避免，不要查全部数据要分批操作；还要防止bigkey中的值集中过期

+ 避免使用耗性能的命令（如：keys、flushall、flushdb），注意 hgetall、smembers 等命令适用场景

+ 无关的业务都尽量拆分到不同Redis实例存储

+ 配置客户端连接池

  JedisPool重要参数：

  maxTotal	资源池中的最大连接数	8	
  maxIdle	资源池允许的最大空闲连接数	8	
  minIdle	资源池确保的最少空闲连接数	0	
  blockWhenExhausted	当资源池用尽后，调用者是否要等待。只有当值为true时，下面的maxWaitMillis才会生效。	true	建议使用默认值。
  maxWaitMillis	当资源池连接用尽后，调用者的最大等待时间（单位为毫秒）。	-1（表示永不超时）	不建议使用默认值。
  testOnBorrow	向资源池借用连接时是否做连接有效性检测（ping）。检测到的无效连接将会被移除。	false	业务量很大时候建议设置为false，减少一次ping的开销。
  testOnReturn	向资源池归还连接时是否做连接有效性检测（ping）。检测到无效连接将会被移除。	false	业务量很大时候建议设置为false，减少一次ping的开销。
  jmxEnabled	是否开启JMX监控	true	建议开启，请注意应用本身也需要开启。

+ 高并发下建议Redis客户端添加熔断功能(例如sentinel、hystrix)

+ 选择合理的清除策略机制

  被动删除 -> 主动删除 -> 主动清理。

  


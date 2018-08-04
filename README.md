# Redis

参考：  
《Redis设计与实现》  
Jedis Client API: https://github.com/xetorthio/jedis (包含了jedis源码[位于src文件夹]和使用代码[位于test文件夹])  
https://blog.csdn.net/kingcat666/article/details/77936970 Java Jedis操作Redis示例（四）——Redis和Mysql的结合方案演进  

Redis实际中高级应用细节也很多的，网上也没有总结实际应用的源码，非一朝一夕，慢慢积累吧，后面将每一种应用场景都实现一下，加深记忆和理解！

Redis基础知识有道云笔记路径：web后端->web后端分层->数据持久层->Redis  
《Redis教程》(Redis应用场景、优缺点（或者说与其他数据库的不同点）、安装启动测试、命令行操作[数据库连接、Redis五种数据类型、
键的管理、五种数据类型的键值对增删改查等操作])  
《Redis实现原理》

## Redis 的应用场景
每个东西被开发出来肯定是为了解决一些问题，或简化或优化一些东西，展示出来就是和其他同类框架不一样的功能或特点。

+ 缓存（数据的查询，新闻，商品的内容，聊天室软件的好友列表）  
    所谓缓存是临时创建用于增加访问速度的数据，比较耗时且不频繁变动的存取操作就比较适合放进缓存。  
+ 任务队列或者叫缓冲（抢购）  
+ 网站访问统计  
    有自增计数  
+ 数据过期策略与内存淘汰机制 （Session自动清理）  
    Redis采用定期删除与懒惰删除策略（定期删除：数据虽然过期，但是我不真的立即删除，到一定的时间（100ms一周期）等要删的数据积累的差不多了随机检查几个key是否过期然后删除，这样容易积累很多过期数据；然后采用懒惰删除策略在获取key的时候再检查一次是否过期，过期了就立即删了），这个策略还是有问题的，需要再配合内存淘汰机制（6中策略机制）
    因为过期管理功能常用于 Session 的缓存，当Session过期的时候，自动清理 Session 数据，免于手动管理。
+ 分布式集群架构中的session分离

+ Redis与MySQL配合使用，互相弥补不足  
    Redis的特点读写速度快，MySQL相对于Redis优点是存储数据量大，可以存储关系复杂的数据（所以MySQL中关系简单且读写频繁的数据都可以交给Redis处理，然后更新到MySQL）；  
    Redis作为MySQL缓存需要面对数据一致性的问题。  

不同数据类型的应用场景
+ String  
    一些复杂的计数功能的缓存  
+ Hash  
    存放结构化数据的场景  
+ List  
    实现消息队列的功能  
    通过lrange命令做分页功能  
+ Set  
    全局去重的功能；获取共同关注的功能（取交集，如果是关系型数据库要先join,然后distinct,比较麻烦）    
    交集、并集、差集计算共同点，全部特点，独有的特点等  
+ Sorted Set  
    通过权重参数做排行榜应用  

## Java命令行应用使用Redis
笔记《Redis教程》中提到的命令行操作，都有对应的Jedis接口。

#### 引入依赖
```
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>2.9.0</version>
    <type>jar</type>
    <scope>compile</scope>
</dependency>
```

#### Jedis基本操作
查看测试代码 test/java/top/kwseeker/commandline/CommandLineAppTest.java

+ 连接  
    Class: redis.clients.jedis.Connection  

    涉及问题：  
    Jedis连接设置与方式、连哪个数据库、认证等问题；

    每个连接都包含以下配置项,连接基于Socket实现，支持SSL连接；  
    ```
    host: "localhost"(default)
    port: "6379"(default)
    connectionTimeout: 2000(default)
    soTimeout: 2000(default)    //读取输入流的超时时间ms
    ssl: false(default)
    ``` 

    SSL连接

    连接方式（通过Jedis对象连接、JedisPool连接、Jedis集群连接）

    连接哪个数据库  
    使用Jedis.select()方法，集群部署中select()是无效的，因为默认使用db0。

    非本地连接的认证处理  
    使用Jedis.auth()方法。
    
    

## Spring应用集成Redis

#### 集成Redis方法
Spring MVC 可以添加依赖 spring-data-redis  
Spring Boot 可以添加依赖 spring-boot-starter-redis

#### Jedis集群

#### 实际应用案例

+ 拦截器与Redis配合实现网站访问计数

+ 作为MySQl的数据缓存



# Redis系统认知

知识学过之后感觉很零散，还经常忘,很多资料也从来没有从设计思想层面系统讲解过Redis设计。但是要形成长期记忆必须将知识串起来，理解设计始末；下面从设计思想角度按自己的理解重新整合下Redis的内容。

持续更新中...

推荐资料:  
《Redis in action / Redis实战》：列举了很多实际应用场景,结合自己项目中需求可能汲取到一些实现建议，适合根据这本书里面的应用场景实操；  
《Redis设计与实现》: 从实现原理角度分析Redis设计思想。   
《Redis深度历险 核心原理与应用实践》：列举了比较重要的部分的应用和原理。
《官方文档 https://redis.io　、http://www.redis.cn》：其他的书感觉不如读官方文档，当作手册使用。  
[Redis源码](https://github.com/antirez/redis)  
[如何阅读 Redis 源码](http://blog.huangz.me/diary/2014/how-to-read-redis-source-code.html)  
最重要的命令: `config get *`

逻辑：  
1. 支持存储哪些数据类型，怎么存储的，怎么做到访问高性能的？
2. 作为内存存储数据库，数据超过存储上限怎么办？内存数据在断电宕机等情况可能存在数据丢失怎么解决？


[TOC]

## Redis定位

键值数据库、高速缓存、消息中间件（这个有点牵强）。

### 键值对如何存储

首先作为一个键值数据库需要最少要实现基本的CRUD。
那么数据键值对数据使用什么数据结构存储？

#### 键值对的存储

SDS(Redis没有使用C语言传统的字符串存储字符串而是定义了一个struct)简单动态字符串;
```C
//类似java ArrayList
struct sdshdr {
  int len;
  int free;
  char buf[];
}
```

key的存储：底层是在SDS中保存, 比如`set name lee`,　redis里面就有个
sdshdr变量buf中存着"name";

value的存储(典型的存储结构，但是不仅仅限于这几种，详细参考《Redis设计与实现》表8-4):  
**String类型**：也是使用SDS变量存储；  
**List类型**：外边看是一个Ｃ语言实现的双向链表，而每个节点是SDS变量；  
**Hash类型**：存储类似Java HashMap,很多key-value,存储结构是Hash桶，每个key、value是SDS变量；
```
hset kwseeker name lee age 26 
```
**Set类型**：存储类似Java HashSet（key插入时要判断是否重复，value是一个固定的Object对象）,每个key是SDS, value是null。  
**ZSet类型**：存储基于hash字典＋跳表（既是hash字典又是跳表），hash字典的key是SDS，一方面保证了内部 value 的唯一性,另一方面它可以给每个 value 赋予一个 score ,代表这个 value 的排序权重。
```java
struct zsl {
  //跳表
  zslnode* header;
  //跳表当前的最高层
  int maxLevel;
  //hash表
  map<string, zslnode 与 ht;
}
```
知道了key value分别是怎么存储的，那么key value是怎么关联起来的？又是什么数据结构？

key-value是用字典实现的,本质是hash表，就是类似java HashMap / Hashtable 的结构。

Redis字典的hash表的实现估计是借鉴了高等语言的容器类，也包含扩容等操作。

最终数据在Redis存储的样子是这样的：
```
redis> SET message "hello world"
redis> RPUSH alphabet "a" "b" "c"
redis> HSET book name "Redis in Action"
redis> HSET book author "Josiah L.Carlson"
redis> HSET book publisher "manning"
```
![](picture/Redis数据存储结构.png)

redisDb的结构
```C
typedef struct redisDb {
    //键空间字典，用于存储及索引键对应的值
    dict *dict;                 /* The keyspace for this DB */
    //保存键空间中包含过期时间的键和过期时间
    dict *expires;              /* Timeout of keys with a timeout set */
    dict *blocking_keys;        /* Keys with clients waiting for data (BLPOP)*/
    dict *ready_keys;           /* Blocked keys that received a PUSH */
    dict *watched_keys;         /* WATCHED keys for MULTI/EXEC CAS */
    int id;                     /* Database ID */
    long long avg_ttl;          /* Average TTL, just for stats */
    unsigned long expires_cursor; /* Cursor of the active expire cycle. */
    list *defrag_later;         /* List of key names to attempt to defrag one by one, gradually. */
} redisDb;
```

PS: 关于跳表,在数据结构与算法仓库中讲解,提供了Java实现版本。

#### 对象与基于引用计数的内存回收机制

为何在前面的基础类型之上又重新封装一层，封装成对象？

基于上面的数据结构创建了一个对象系统。对象系统作用：  
１）执行命令之前可以根据对象的类型来判断一个对象是否可以执行给定的命令。  
２）针对不同使用场景为对象设置多种不同的数据结构实现，优化对象在不同场景下的使用效率。

```C
typedef struct redisObject {
    //对象类型：REDIS_STRING REDIS_LIST REDIS_HASH REDIS_SET REDIS_ZSET 
    unsigned type:4;
    //编码（底层数据结构类型编码）
    unsigned encoding:4;
    //应该是删除策略使用的？
    unsigned lru:LRU_BITS; /* LRU time (relative to global lru_clock) or
                            * LFU data (least significant 8 bits frequency
                            * and most significant 16 bits access time). */
    //引用计数，用于实现自动回收
    int refcount;
    //底层数据结构，就是上一小节讲的各种类型数据结构
    void *ptr;
} robj;
```

Redis对键和值分别定义一个对象，键总是字符串对象，值则有很多数据结构类型，并不限于上面的５种。  
总地来说是对整型数据做了优化。
![](picture/Redis对象与数据结构.jpg)

TODO: 使用Redis对象及不同数据结构具体有哪些好处（书上也没有说清具体有什么好处）？或者Redis为什么要对整型数据进行存储优化？

##### Redis使用的内存存储，势必不能存储大量数据，当缓存已满如何处理

最大内存存储可以在redis.conf中设置，或通过`CONFIG SET`设置：
```
maxmemory 100mb
```

Redis针对这种情况有６种淘汰策略(maxmemory-policy,主要分为对设置了expire的键和allkey)，按使用热度排列：  
```
volatile-lru      //针对设置了超时时间的键，删除最近最少使用的键
allkeys-lru       //针对所有的键，删除最近最少使用的键
volatile-ttl      //针对设置了超时时间的键，删除快要过期的键
volatile-random   //针对设置了超时时间的键，随机删除
allkeys-random    //针对所有的键，随机删除
noeviction        //不删除，达到最大内存限制直接返回错误
```

如何合理选择策略：通过INFO命令监控缓存命中率。

#### 键值对的基本操作原理

##### C、S建立连接到发送指令再到数据写入内存的流程

+ ++前期准备++

  下载Redis源码，找到代码入口；
  ```shell
  grep -rins "main(" ./redis/src
  ```
  找到如下结果
  ```shell
  # 客户端入口
  ./redis-cli.c:7884:int main(int argc, char **argv) {
  #
  ./dict.c:1184:int main(int argc, char **argv) {
  #
  ./sds.c:1297:int main(void) {
  #
  ./siphash.c:351:int main(void) {
  #
  ./redis-benchmark.c:1475:int main(int argc, const char **argv) {
  #
  ./localtime.c:109:int main(void) {
  # 服务端入口
  ./server.c:4889:int main(int argc, char **argv) {
  ```

+ ++CS连接++  

  Jedis客户端和Redis服务器通过基于TCP实现的RESP协议实现连接交互。

  TODO: RESP协议原理？



+ ++Redis-server解析收到指令并执行++

  Redis-server中据说使用到IO多路复用，那么应该就是在建立连接之后处理请求这部分。

##### 排序

#### 事务的实现

### 作为一个高速缓存如何提升访问效率

#### 事件驱动模型

### 消息中间件怎么设计

#### 消息的发布与订阅

### 作为服务总也绕不开的数据安全和高可用如何保证

#### [数据安全：持久化](https://redis.io/topics/persistence)

由于Redis是内存数据库，为了防止断电、宕机等问题导致数据丢失，需要做数据持久化，保存到硬盘；  
Redis-server每次启动会先加载.rdb文件，将数据加载到内存中。

##### RDB（默认持久化方式）

将当前数据生成.rdb快照保存到硬盘；有两个命令`save`和`bgsave`，支持手动和自动:
![](picture/RDB_save流程.png)
save使用主进程进行持久化，会阻塞其他操作。
```
//设置快照文件 dump.rdb 存储位置
//也可在redis.conf中配置　
//  dbfilename dump.rdb
//  dir /usr/local
redis> config set dbfilename dump.rdb
redis> config set dir /usr/local
redis> save
```

![](picture/RDB_bgsave流程.png)
bgsave会新创建一个线程，在这个线程中完成持久化操作。
```
redis> bgsave
```

##### AOF

存储命令而不是数据，和MySQL binlog类似。

开启AOF持久化,redis.conf中配置
```
appendonly yes
appendfilename "appendfilename.aof"
//持久化策略
appendsync always   //每次有新命令都会追加到aof文件中,不会丢失数据、安全，但性能低
appendsync everysec //每秒向aof文件中存储一下这秒中的命令，折中性能和数据安全,最多可能丢失１s的数据
appendsync no
```

##### RDB与AOF对比

根据数据是否容许丢失选择持久化模式。如果只是做缓存直接使用RDB即可。
如果是实时排名则应选择AOF，两者也可以同时使用，并不冲突。

RDB:  
全量数据（压缩二进制文件），恢复数据速度快；  
但是无法实时持久化，容易丢数据。  

AOF:  
相对RDB不容易丢失数据；保存了所有写入操作，文件可读；不用担心命令执行失败丢失数据；AOF文件；  
但是AOF文件比RDB更大；持久化效率比RDB慢；数据恢复慢。

#### 高可用：集群部署

##### 集群实现方案

+ **主从模式**

  Master写，Slave读；Master宕机，Slave无法切为Master导致不可用。需要手动将Slave切换为Master。

  redis.conf
  ```
  # 主从配置，将当前实例作为<masterip>:<masterport>的从机
  replicaof <masterip> <masterport>
  ```
  或者命令行
  ```
  ./redis-server ../redis7000.conf --slaveof 192.168.0.31 6379 &
  ```
  通过`info replication`可以查看主从状态。

+ **哨兵模式**

  在主从模式基础上启动哨兵对所有节点进行监听。部署参考deploy中docker-compose.yml。

  监控集群中服务器状态，当主机挂掉之后，可以进行选举从从机中选举出一个新的主机。

  哨兵其实是代理，有哨兵时，客户端不再直接连接Redis节点，而是通过哨兵间接连接Redis节点。哨兵与Redis节点有心跳连接用于监控节点健康状态。




+ **集群模式**

##### 数据一致性问题

##### 主服务器宕机重新选举、故障转移

### 其他

#### 使用Lua脚本拓展功能
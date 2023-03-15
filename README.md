# Redis 源码分析 & 应用总结

所有文档都放在 docs 目录下。

源码流程图参考 graph 目录。



## Redis知识点

+ 基础概念
  + Redis特点
  + Redis为什么快
  + Redis使用场景
+ 数据结构
  + 5种基本数据类型
  + 3种特殊类型
  + Stream类型
  + 对象机制
  + 底层数据结构

+ 拓展核心功能
  + 持久化
  + 订阅/发布
  + 事件机制
  + 事务
+ 高可用/可拓展
  + 主从复制
  + 哨兵机制（Sentinel）
  + 分片技术（Cluster）



## Redis 源码分析

+ 01-源码环境搭建
+ [03-Redis数据结构&内部实现原理](docs/03-Redis数据结构&内部实现原理.md)



## 应用总结

+ 分布式锁实现
+ 布隆过滤器
+ 场景问题&解决
  + 缓存穿透
  + 缓存击穿
  + 缓存雪崩
  + 数据库与缓存一致性问题
    + cache-aside
    + read-through 
    + write-through
    + write-behind
    + refresh-ahead
  + 热key缓存过期后重建问题
+ Spring集成
+ 监控与优化

# Redis内存分析工具

对Redis内存占用进行优化时，需要分析内存中有哪些Key，哪些Key或哪类Key占用户内存最多，有多少个，着重对这些Key进行优化。

开源的内存分析工具：

+ 静态分析
  + [RDR](https://github.com/xueqiu/rdr)（redis data reveal）
  + [rdbtools](https://github.com/sripathikrishnan/redis-rdb-tools)（redis-rdb-tools）

  > 注意静态工具内存统计和redis info 命令都有偏差，注意校对。

+ 实时分析
  + RMA（Redis Memory Analyzer）
  + Redis Sampler
  + Redis-Audit
  + Harvest
  + RedisInsight

参考资料：

+ [The Top 6 Free Redis Memory Analysis Tools](https://scalegrid.io/blog/the-top-6-free-redis-memory-analysis-tools/)

  [中文翻译](https://devpress.csdn.net/redis/62f0cce67e668234661831b0.html)



## RDR

RDR是一个解析redis rdbfile的工具(Go语言开发)，即使用这个工具需要下载rdb文件。与 redis-rdb-tools 相比，RDR 由 golang 实现，速度更快（5GB rdbfile 在作者的 PC 上大约需要 2 分钟）。

**安装**：

```shell
wget https://github.com/xueqiu/rdr/releases/download/v0.0.1/rdr-linux -O rdr
```

**使用**：

```txt
rdr [global options] command [command options] [arguments...]
```

当前版本只有四个命令：

+ dump

  dump rdb文件静态信息到标准输出(STDOUT)

+ show    

   通过网页展示rdb文件静态信息

  如：

  ```shell
  rdr show -p 8010 dump.rdb 
  ```

+ keys     

  从rdb文件读取所有key信息

+ help, h 

  显示帮助信息

**RDR vs 腾讯云Redis监控**：

腾讯云Redis监控主要的不足是没有对以某个前缀开头的key的数量和内存占用的统计。

但是RDR中有个窗口`count by key prefix`则实现了上面的需求。



## rdbtools

Rdbtools是Redis dump.rdb文件的解析器。解析器生成的事件类似于xml sax解析器，并且在内存方面非常高效。

Rdbtools是用Python开发的。

In addition, rdbtools provides utilities to :

1. Generate a Memory Report of your data across all databases and keys
2. Convert dump files to JSON
3. Compare two dump files using standard diff tools

**安装**：

```shell
# 官方推荐，说比通过源码安装的有更高的解析速度
pip install rdbtools python-lzf
```

**使用**：

[README](https://github.com/sripathikrishnan/redis-rdb-tools)

比如，查看编号3的数据库中以“House_TaskDaily:”开头的所有Key的内存占用信息导出到keys.csv文件。

```shell
rdb -c memory -n 3 -k "House_TaskDaily:*" dump.rdb > keys.csv
```

详细文档：

```
usage: usage: rdb [options] /path/to/dump.rdb

Example : rdb --command json -k "user.*" /var/redis/6379/dump.rdb

positional arguments:
  dump_file             RDB Dump file to process

optional arguments:
  -h, --help            show this help message and exit
  -c CMD, --command CMD
                        Command to execute. Valid commands are json, diff,
                        justkeys, justkeyvals, memory and protocol
  -f FILE, --file FILE  Output file
  -n DBS, --db DBS      Database Number. Multiple databases can be provided.
                        If not specified, all databases will be included.
  -k KEYS, --key KEYS   Keys to export. This can be a regular expression
  -o NOT_KEYS, --not-key NOT_KEYS
                        Keys Not to export. This can be a regular expression
  -t TYPES, --type TYPES
                        Data types to include. Possible values are string,
                        hash, set, sortedset, list. Multiple typees can be
                        provided. If not specified, all data types will be
                        returned
  -b BYTES, --bytes BYTES
                        Limit memory output to keys greater to or equal to
                        this value (in bytes)
  -l LARGEST, --largest LARGEST
                        Limit memory output to only the top N keys (by size)
  -e {raw,print,utf8,base64}, --escape {raw,print,utf8,base64}
                        Escape strings to encoding: raw (default), print,
                        utf8, or base64.
  -x, --no-expire       With protocol command, remove expiry from all keys
  -a N, --amend-expire N
                        With protocol command, add N seconds to key expiry
                        time

```

**rdbtools vs RDR**

据说没RDR快，但是看使用上比RDR功能更丰富更灵活，比如可以统计以某个前缀开头的所有key的信息。


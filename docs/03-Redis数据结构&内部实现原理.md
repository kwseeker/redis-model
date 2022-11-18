# Redis数据结构&内部实现原理

+ Redis数据类型
  + String
  + List
  + Set
  + ZSet
  + Hash
  + Bitmap
  + GenHash
  + HyperLogLog
  + Stream （5.0之后新增）
+ Redis内部数据结构
  + int

+ 数据类型与内部数据结构对应关系


> 源码版本：Redis 6.2

## String

以`set`命令为例，源码设置值的流程：

```c
// 完整命令 SET key value [NX] [XX] [KEEPTTL] [GET] [EX <seconds>] [PX <milliseconds>] 缺省部分按默认值处理
// 省略了不重要的部分（编码、内存分配等等）
void setCommand(client *c)
    setGenericCommand(c,flags,c->argv[1],c->argv[2],expire,unit,NULL,NULL)
    	genericSetKey(c,c->db,key,val,flags & OBJ_SET_KEEPTTL,1);

void genericSetKey(client *c, redisDb *db, robj *key, robj *val, int keepttl, int signal) {
    if (lookupKeyWrite(db,key) == NULL) {
        dbAdd(db,key,val);
    } else {
        dbOverwrite(db,key,val);
    }
    incrRefCount(val);
    if (!keepttl) removeExpire(db,key);
    if (signal) signalModifiedKey(c,db,key);
}

sds copy = sdsdup(key->ptr);
int retval = dictAdd(db->dict, copy, val);
```




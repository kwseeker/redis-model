# Redis数据结构&内部实现原理

> 源码版本：Redis 6.2.6



## Redis数据类型与内部结构

### Redis数据类型

#### String、List、Set、ZSet、Hash

```C
#define OBJ_STRING 0    /* String object. */
#define OBJ_LIST 1      /* List object. */
#define OBJ_SET 2       /* Set object. */
#define OBJ_ZSET 3      /* Sorted set object. */
#define OBJ_HASH 4      /* Hash object. */
```

#### Bitmap

#### GenHash

#### HyperLogLog

#### Stream （5.0之后新增）

### Redis内部数据结构

```C
#define OBJ_ENCODING_RAW 0     /* Raw representation */
#define OBJ_ENCODING_INT 1     /* Encoded as integer */
#define OBJ_ENCODING_HT 2      /* Encoded as hash table */
#define OBJ_ENCODING_ZIPMAP 3  /* Encoded as zipmap */
#define OBJ_ENCODING_LINKEDLIST 4 /* No longer used: old list encoding. */
#define OBJ_ENCODING_ZIPLIST 5 /* Encoded as ziplist */
#define OBJ_ENCODING_INTSET 6  /* Encoded as intset */
#define OBJ_ENCODING_SKIPLIST 7  /* Encoded as skiplist */
#define OBJ_ENCODING_EMBSTR 8  /* Embedded sds string encoding */
#define OBJ_ENCODING_QUICKLIST 9 /* Encoded as linked list of ziplists */
#define OBJ_ENCODING_STREAM 10 /* Encoded as a radix tree of listpacks */
```

#### RAW

#### INT

#### HT

### 数据类型与内部数据结构对应关系



### **Redis重要结构体**

#### robj 

```C
typedef struct redisObject {
    unsigned type:4;	   //数据类型（占unsigned int 中4位），如：OBJ_STRING...
    unsigned encoding:4;   //编码类型（占unsigned int 中4位），就是内部存储结构，如RAW、INT...
    unsigned lru:LRU_BITS; /* LRU time (relative to global lru_clock) or
                            * LFU data (least significant 8 bits frequency
                            * and most significant 16 bits access time). */
    int refcount; 		   //
    void *ptr;			   //键值对，值的指针，如果是String类型，这里就是sds
} robj;
```

> struct 中 unsigned type:4; 是位操作。

#### sds （Simple Dynamic String）

```C
typedef char *sds;
```

索引为-1的位置上存储的是SDS的类型（有5中sds类型）:

```C
#define SDS_TYPE_5  0
#define SDS_TYPE_8  1
#define SDS_TYPE_16 2
#define SDS_TYPE_32 3
#define SDS_TYPE_64 4
#define SDS_TYPE_MASK 7

//flags低3位保存SDS类型，高5位保存字符串长度
//buf是字符串内容
struct __attribute__ ((__packed__)) sdshdr5 {
    unsigned char flags; /* 3 lsb of type, and 5 msb of string length */
    char buf[];
};
//低3位保存类型，高5位保存字符串长度
//len是无符号char类型，即最大存储长度255
//flags低3位保存SDS类型，高5位没有使用
struct __attribute__ ((__packed__)) sdshdr8 {
    uint8_t len; /* used */
    uint8_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};
struct __attribute__ ((__packed__)) sdshdr16 {
    uint16_t len; /* used */
    uint16_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};
struct __attribute__ ((__packed__)) sdshdr32 {
    uint32_t len; /* used */
    uint32_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};
struct __attribute__ ((__packed__)) sdshdr64 {
    uint64_t len; /* used */
    uint64_t alloc; /* excluding the header and null terminator */
    unsigned char flags; /* 3 lsb of type, 5 unused bits */
    char buf[];
};
```

上面可以看到 flags 紧挨着 buf 定义，因为 robj 中 ptr 指针指向 buf , 要用 

```C
unsigned char flags = s[-1];
//将指针从buf向上移动，指向len,然后转换指针类型（如sdshdr8）
#define SDS_HDR(T,s) ((struct sdshdr##T *)((s)-(sizeof(struct sdshdr##T))))	//##是粘连符号
```

获取sds flag, 即SDS具体类型、甚至是字符串长度。



## String

以`set`命令为例。

### 源码set处理流程

1. 调用栈

   ```c
   setCommand t_string.c:266	//进入string set 命令处理
   call server.c:3721
   processCommand server.c:4241
   processCommandAndResetClient networking.c:2039
   processInputBuffer networking.c:2140
   readQueryFromClient networking.c:2226
   callHandler connhelpers.h:79
   connSocketEventHandler connection.c:295
   aeProcessEvents ae.c:427
   aeMain ae.c:487
   main server.c:6401
   ```

2. set命令处理分析

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






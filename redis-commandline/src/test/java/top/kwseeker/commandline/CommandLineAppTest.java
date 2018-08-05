package top.kwseeker.commandline;

import org.junit.*;
import org.junit.runners.MethodSorters;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import top.kwseeker.commandline.JedisPromote.JedisPro;
import top.kwseeker.commandline.constant.ConfigParam;

import java.util.*;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommandLineAppTest {

    private static Jedis jedis = null;

    @BeforeClass
    public static void initData() {
        System.out.println("Jedis initData..");
        //建立连接
        jedis = new Jedis();      //使用默认参数，具体参考README.md
        jedis.auth(ConfigParam.PASSWORD);           //密码认证
        assertEquals("Connect failed..", "PONG", jedis.ping());

        //清空库中所有的数据(我们以15号数据库作为测试数据库)
        jedis.select(ConfigParam.TEST_DB_INDEX);
        assertEquals(15, jedis.getDB().longValue());
        //jedis.flushAll();             //清空所有数据库
        jedis.flushDB();                //清空当前数据库

        //数据初始化（假设添加几个学生和老师的信息）
        jedis.set("className", "GodLevelClass");
        jedis.set("teacherCount", "4");
        jedis.set("studentCount", "7");
        //jedis.lpush("students", "Arvin", "Bob", "Cindy", "David", "Eric", "Frank", "Grace");    //TODO: Jedis lpush方法明明是变长的参数却没法用？
        //jedis.lpush("teachers", "Gauss", "Einstein", "Joseph", "Linus");
        //JedisPro.lpush(jedis, "students", "Arvin", "Bob", "Cindy", "David", "Eric", "Frank", "Grace");
        //JedisPro.lpush(jedis, "teachers","Gauss", "Einstein", "Joseph", "Linus");
        jedis.sadd("students", "Arvin", "Bob", "Cindy", "David", "Eric", "Frank", "Grace");
        jedis.sadd("teachers","Gauss", "Einstein", "Joseph", "Linus");
    }

    @AfterClass
    public static void jedisClose() {
        System.out.println("Jedis close..");
        jedis.close();
    }

    /**
     * DEL
     * DUMP
     * EXISTS
     * EXPIRE
     * EXPIREAT
     * KEYS
     * MOVE
     * PERSIST
     * PTTL
     * TTL
     * RANDOMKEY
     * RENAME
     * RENAMENX
     * TYPE
     *
     * http://www.redis.net.cn/tutorial/3507.html
     */
    @Test
    public void AKeyOperateTest() {
        System.out.println("Jedis Key operations..");
        //查询所有key（KEYS）
        Set<String> keys = jedis.keys("*");
        assertEquals(5, keys.size());
        Iterator<String> iterator = keys.iterator();
        StringBuilder sb = new StringBuilder(); //单线程使用StringBuilder,多线程使用StringBuffer
        while (iterator.hasNext()) {
            sb.append(" ");
            sb.append(iterator.next());
        }
        System.out.println("All keys:" + "\n" + sb);

        //判断某个key是否存在
        assertTrue("key {teachers} is not exist", jedis.exists("teachers"));
        //新增三个String键值对
        jedis.set("courseCount", "4");
        JedisPro.lpush(jedis,"teachTask", "math", "physics", "chemistry", "computer science");
        jedis.set("other", "something");
        assertEquals(8, jedis.keys("*").size());
        //删除键值对
        jedis.del("other");
        assertEquals(7, jedis.keys("*").size());
        //设置courses过期时间为30s
        jedis.expire("teachTask", 5);    //课程表过期时间为5s
        jedis.expire("courseCount", 5);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("remaining lifetime: " + jedis.ttl("teachTask"));
        jedis.persist("courseCount");   //移除过期时间，永久保存，但是重启还是会消失，除非持久化存储
        System.out.println("remaining lifetime: " + jedis.ttl("courseCount"));
        try {
            //Thread.sleep(3000);
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertFalse(jedis.exists("teachTask"));
        //查看key值的存储类型
        assertEquals("list", jedis.type("teachers"));
        //随机返回一个key
        System.out.println("random key: " + jedis.randomKey());
        //将key移动到14号数据库
        jedis.move("courseCount", 14);
    }

    /**
     * SET
     * GET
     * GETRANGE (获取字符串某段子字符串)
     * GETSET
     * GETBIT (获取字符串某bit的值)
     * MGET (multi get 一次性获取多个值)
     * SETBIT (设置字符串某bit的值)
     * SETEX (set expire 带过期时间的set)
     * SETNX (set not exist)
     * SETRANGE (设置字符串某偏移量上字节)
     * STRLEN
     * MSET (multi set)
     * MSETNX (当且仅当所有给定 key 都不存在时的MSET)
     * PSETEX (以毫秒为单位设置 key 的生存时间的SETEX)
     * INCR (整数加1)
     * INCRBY
     * INCRBYFLOAT (浮点数加)
     * DECR
     * DECRBY
     * APPEND
     */
    @Test
    public void BStringOperateTest() {
        System.out.println("Jedis String operations..");

        jedis.set("teacherCount", "5"); //键值对已存在，则是修改
        String classLevel = jedis.getrange("className", 0, 7);
        List<String> valueList = jedis.mget("teacherCount", "studentCount");
        jedis.incr("teacherCount");
        jedis.incrBy("teacherCount", 2);
        assertEquals(8L, Long.valueOf(jedis.get("teacherCount")).longValue());
        jedis.decr("teacherCount");
        jedis.decrBy("teacherCount", 2);

        jedis.append("className", "(9.5)");
    }

    /**
     * BLPOP (Block left pop)
     * BRPOP (Block right pop)
     * BRPOPLPUSH (Block right pop left push)
     * LINDEX (list index)
     * LINSERT (list insert)
     * LLEN
     * LPOP
     * LPUSH
     * LPUSHX (Left push if exist)
     * LRANGE
     * LREM (移除列表元素)
     * LSET (通过检索设置列表元素的值)
     * LTRIM (只保留指定区间里面的元素)
     * RPOP
     * RPOPLPUSH
     * RPUSH
     * RPUSHX
     *
     * 任务类型的数据适合存储到 List
     */
    @Test
    public void CListOperateTest() {
        System.out.println("Jedis List operations..");

        JedisPro.lpush(jedis,"teachTask", "math", "physics", "chemistry", "computer science");
        assertEquals(4L, jedis.llen("teachTask").longValue());
        assertEquals("physics", jedis.lindex("teachTask", 1));
        jedis.linsert("teachTask", BinaryClient.LIST_POSITION.AFTER, jedis.lindex("teachTask", 2L), "math");
        jedis.lrem("teachTask", 1L, "math");    //从左（头）向右删除1个math
        jedis.ltrim("teachTask", 1L, 3L);           //保留1,3之间的元素
        int len = jedis.llen("teachTask").intValue();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<len; i++) {
            sb.append(" ");
            sb.append(jedis.lindex("teachTask", i));
        }
        System.out.println(sb);
    }

    /**
     * SADD
     * SCARD (获取集合的成员数)
     * SDIFF (返回给定所有集合的差集)
     * SDIFFSTORE (差集存储在新的集合) SINTER (求交集)
     * SISMEMBER
     * SMEMBERS (所有成员)
     * SMOVE
     * SPOP (随机移除并返回集合中的一个元素)
     * SRANDMEMBER (返回集合中一个或多个随机数)
     * SREM (移除集合中一个或多个成员) SUNION (求并集)
     * SSCAN (迭代集合中的元素)
     */
    @Test
    public void DSetOperateTest() {
        System.out.println("Jedis Set operations..");

        assertEquals(4L, jedis.scard("teachers").longValue());

        //基本操作比较简单，略
    }

    /**
     * ZADD
     * ZCARD (获取有序集合的成员数)
     * ZCOUNT (计算在有序集合中指定区间分数的成员数)
     * ZINCRBY (有序集合中对指定成员的分数加上增量 increment)
     * ZINTERSTORE
     * ZRANGE (通过索引区间返回有序集合成指定区间内的成员)
     * ZRANGEBYLEX (通过字典区间返回有序集合的成员) ZRANGEBYSCORE
     * ZLEXCOUNT ZRANK
     * ZREM
     * ZREMRANGERBYLEX (移除有序集合中给定的字典区间的所有成员) ZREMRANGEBYRANK (移除有序集合中给定的排名区间的所有成员)
     * ZREMRANGEBYSCORE (移除有序集合中给定的分数区间的所有成员)
     * ZREVRANGE (返回有序集中指定区间内的成员，通过索引，分数从高到底) ZREVRANGEBYSCORE (返回有序集中指定分数区间内的成员，分数从高到低排序)
     * ZREVRANK (返回有序集合中指定成员的排名，有序集成员按分数值递减(从大到小)排序)
     * ZSCORE (返回有序集中，成员的分数值)
     * ZUNIONSTORE (计算给定的一个或多个有序集的并集，并存储在新的 key 中)
     * ZSCAN (迭代有序集合中的元素（包括元素成员和元素分值）)
     */
    @Test
    public void ESortedSetOperateTest() {
        System.out.println("Jedis SortedSet operations..");

        //基本操作比较简单，略
    }

    /**
     * TODO: Hash虽说适用于存储对象，但是如果对象比较复杂（ 成员也是对象）又该怎么存储？拆分然后通过某个键关联么？
     */
    @Test
    public void FHashOperateTest() {
        System.out.println("Jedis Hash operations..");

        // "Arvin", "Bob", "Cindy", "David", "Eric", "Frank", "Grace"
        //拆分学生信息和成绩，成绩通过信息的gradeFK的值进行关联
        Map<String, String> arvinMap = new HashMap<>();
        arvinMap.put("name", "Arvin");
        arvinMap.put("age", "25");
        arvinMap.put("sex", "male");
        arvinMap.put("gradeFK", "arvinGrade");
        jedis.hmset("ArvinHashKey", arvinMap);
        Map<String, String> arvinGradeMap = new HashMap<>();
        arvinGradeMap.put("mathGrade", "C");
        arvinGradeMap.put("physicsGrade", "A");
        arvinGradeMap.put("chemistryGrade", "B");
        arvinGradeMap.put("computerScienceGrade", "B");
        jedis.hmset(jedis.hget("ArvinHashKey", "gradeFK"), arvinGradeMap);
        jedis.hset(jedis.hget("ArvinHashKey", "gradeFK"), "computerScienceGrade", "A");

        String arvinGradeKey = jedis.hget("ArvinHashKey", "gradeFK");
        List<String> gradeList = jedis.hmget(arvinGradeKey, "mathGrade", "computerScienceGrade");
        Map<String, String> aMap = jedis.hgetAll("ArvinHashKey");
        Map<String, String> bMap = jedis.hgetAll(arvinGradeKey);
    }

}
package top.kwseeker.commandline;

import org.junit.*;
import org.junit.runners.MethodSorters;
import redis.clients.jedis.Jedis;
import top.kwseeker.commandline.JedisPromote.JedisPro;
import top.kwseeker.commandline.constant.ConfigParam;

import java.util.Iterator;
import java.util.Set;

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
        //jedis.lpush("students", "Arvin", "Bob", "Cindy", "David", "Eric", "Frank", "Grace");    //TODO: Jedis lpush方法明明是变长的参数却没法用？
        //jedis.lpush("teachers", "Gauss", "Einstein", "Joseph", "Linus");
        jedis.set("className", "GodLevelClass");
        jedis.set("teacherCount", "4");
        jedis.set("studentCount", "7");
        JedisPro.lpush(jedis, "students", "Arvin", "Bob", "Cindy", "David", "Eric", "Frank", "Grace");
        JedisPro.lpush(jedis, "teachers","Gauss", "Einstein", "Joseph", "Linus");
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
        JedisPro.lpush(jedis,"courses", "math", "physics", "chemistry", "computer science");
        jedis.set("other", "something");
        assertEquals(8, jedis.keys("*").size());
        //删除键值对
        jedis.del("other");
        assertEquals(7, jedis.keys("*").size());
        //设置courses过期时间为30s
        jedis.expire("courses", 5);    //每门课程课程时间为5s
        jedis.expire("courseCount", 5);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("remaining lifetime: " + jedis.ttl("courses"));
        jedis.persist("courseCount");   //移除过期时间，永久保存
        System.out.println("remaining lifetime: " + jedis.ttl("courseCount"));
        try {
            //Thread.sleep(3000);
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertFalse(jedis.exists("courses"));
        //查看key值的存储类型
        assertEquals("list", jedis.type("teachers"));
        //随机返回一个key
        System.out.println("random key: " + jedis.randomKey());
        //将key移动到14号数据库
        jedis.move("courseCount", 14);
    }



}
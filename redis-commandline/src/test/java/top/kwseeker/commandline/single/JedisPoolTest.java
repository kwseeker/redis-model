package top.kwseeker.commandline.single;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 连接池
 */
public class JedisPoolTest {

    private HostAndPort hostAndPort = new HostAndPort("localhost", 6379);

    @Test
    public void testJedisPool() {
        final JedisPool jedisPool = new JedisPool(new GenericObjectPoolConfig(),
                hostAndPort.getHost(),
                hostAndPort.getPort(),
                5000,
                "123456");

        final JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(20);              //这四个属性是对象池属性
        config.setMinIdle(5);
        config.setMaxWaitMillis(60*1000);
        config.setTestOnBorrow(false);
        final JedisPool jedisPool1 = new JedisPool(config, "localhost");

        List<Thread> threadList = new ArrayList<>();
        final AtomicInteger operCount = new AtomicInteger();
        long beginTime = System.currentTimeMillis();
        Jedis jedisTemp = jedisPool.getResource();
        System.out.println("测试开始前key数量：" + jedisTemp.dbSize());
        for (int i = 0; i < 50; i++) {
            Thread thread = new Thread(()->{
                for (int j = 0; (j = operCount.getAndIncrement()) < 10000;) {
                    Jedis jedis = jedisPool.getResource();
                    final String key = "foo" + j;
                    jedis.set(key, key);
                    jedis.get(key);
                    jedis.close();
                }
            });
            threadList.add(thread);
            thread.start();
        }

        try {
            for (Thread thread : threadList) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("操作耗时(ms)：" + (System.currentTimeMillis() - beginTime));
        System.out.println("测试结束后key数量：" + jedisTemp.dbSize());
        jedisTemp.close();
        jedisPool.destroy();
    }
}

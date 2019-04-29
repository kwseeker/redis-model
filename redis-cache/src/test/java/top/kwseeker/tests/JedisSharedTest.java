package top.kwseeker.tests;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;

import java.util.ArrayList;
import java.util.List;

/**
 * Jedis 数据分片，读写等操作的原理
 */
public class JedisSharedTest {

    private Connection connector;
    private List<JedisShardInfo> shardInfos;

    @Before
    public void initial() {
        shardInfos = new ArrayList<>(2);
        //定义分片
        //6379
        JedisShardInfo shardInfo1 = new JedisShardInfo(ConnectionSetting.host, ConnectionSetting.port);
        shardInfo1.setPassword(ConnectionSetting.password);
        shardInfos.add(shardInfo1);
        //6378
        JedisShardInfo shardInfo2 = new JedisShardInfo(ConnectionSetting.host, ConnectionSetting.port);
        shardInfo2.setPassword(ConnectionSetting.password);
        shardInfos.add(shardInfo2);
    }

    @Test
    public void testJedisConnectWithShardInfo() {
        //创建分片客户端
        ShardedJedis shardedJedis = new ShardedJedis(shardInfos);
        //指定分片算法
        ShardedJedis shardedJedis1 = new ShardedJedis(shardInfos, Hashing.MURMUR_HASH);

        //数据操作
        shardedJedis.set("A", "a");
        JedisShardInfo ak = shardedJedis.getShardInfo("A");
        shardedJedis.set("B", "b");
        JedisShardInfo bk = shardedJedis.getShardInfo("B");

        //分片服务操作
        // 1)查看连接状态

        // 2)获取不同分片的key

        // 3)key标签

    }

    @Test
    public void testShardJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();     //JedisPool和ShardedJedisPool使用的配置类是相同的
        config.setTestOnBorrow(true);
        ShardedJedisPool shardedJedisPool = new ShardedJedisPool(config, shardInfos);

        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        shardedJedis.set("ShardedJedis_Pool_A", "a");
        shardedJedis.close();
    }
}

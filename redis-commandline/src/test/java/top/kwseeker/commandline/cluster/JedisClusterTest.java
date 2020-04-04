package top.kwseeker.commandline.cluster;

import org.junit.Test;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;

public class JedisClusterTest {

    @Test
    public void testJedisCluster() throws IOException {
        JedisCluster jedisCluster = new JedisCluster(NodesConfig.nodes);
        jedisCluster.set("A", "a");
        System.out.println(jedisCluster.get("A"));
        jedisCluster.close();
    }
}

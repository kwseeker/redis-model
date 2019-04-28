package top.kwseeker.tests;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class JedisClusterTest {

    @Test
    public void testRedisCluster() throws IOException {
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort(ConnectionSetting.host, 6380));
        nodes.add(new HostAndPort(ConnectionSetting.host, 6381));
        nodes.add(new HostAndPort(ConnectionSetting.host, 6382));
        nodes.add(new HostAndPort(ConnectionSetting.host, 6383));
        nodes.add(new HostAndPort(ConnectionSetting.host, 6384));
        nodes.add(new HostAndPort(ConnectionSetting.host, 6385));

        JedisCluster jedisCluster = new JedisCluster(nodes);
        jedisCluster.set("ClusterA", "a");
        assertEquals("a", jedisCluster.get("ClusterA"));
        jedisCluster.close();
    }
}

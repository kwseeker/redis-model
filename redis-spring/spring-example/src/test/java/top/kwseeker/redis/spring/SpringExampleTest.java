package top.kwseeker.redis.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.JedisCluster;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application.xml"})
public class SpringExampleTest {

    @Autowired
    private JedisCluster jedisCluster;

    @Test
    public void testGetKeyValue() {
        System.out.println(jedisCluster.get("clusterTestKey"));
    }
}

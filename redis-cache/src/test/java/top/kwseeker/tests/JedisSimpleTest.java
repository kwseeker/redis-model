package top.kwseeker.tests;

import org.junit.Test;
import redis.clients.jedis.Connection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisSimpleTest {

    private Connection connector;

    @Test
    public void testJedisConnection() {
        connector = new Connection(ConnectionSetting.host, ConnectionSetting.port);
        connector.setConnectionTimeout(ConnectionSetting.connectTimeout);
        connector.connect();
        connector.close();
    }

    @Test(expected = JedisConnectionException.class)
    public void testJedisSimple() {
        Jedis jedis = new Jedis(ConnectionSetting.host, ConnectionSetting.port, ConnectionSetting.connectTimeout);
        try {
            jedis.select(ConnectionSetting.dbIndex);
            jedis.auth(ConnectionSetting.password);
            System.out.println(jedis.dbSize());

            System.out.println(jedis.get("myname"));
            String timeout = jedis.configGet("timeout").get(1);
            jedis.configSet("timeout", String.valueOf(ConnectionSetting.connectIdleTime));
            //jedis.configSet("timeout", timeout);
            Thread.sleep(2100);
            jedis.get("timeout");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }
}

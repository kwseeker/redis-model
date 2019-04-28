package top.kwseeker.cache.connection;

import redis.clients.jedis.Jedis;

public class RedisSingletonConnectorFactory extends AbstractRedisConnectorFactory {



    public Jedis getJedisConnector() {
        return null;
    }

    //关闭连接实例
    public void close() {

    }
}

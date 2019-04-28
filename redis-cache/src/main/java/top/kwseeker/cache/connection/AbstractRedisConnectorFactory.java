package top.kwseeker.cache.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

public abstract class AbstractRedisConnectorFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractRedisConnectorFactory.class);

    private String host = Protocol.DEFAULT_HOST;
    private int port = Protocol.DEFAULT_PORT;
    private String password = "123456";
    private int timeout = Protocol.DEFAULT_TIMEOUT;

    private int dbIndex = 0;

    //获取连接实例
    public abstract Jedis getJedisConnector();

    //关闭连接实例
    public abstract void close();
}

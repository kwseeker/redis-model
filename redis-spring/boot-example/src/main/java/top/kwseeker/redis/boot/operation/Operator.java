package top.kwseeker.redis.boot.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * config中获取连接，然后通过连接的API即可操作集群
 * 当然也可以在　RedisClusterConnection　基础上封装新的接口
 */
@Component
public class Operator {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    public Boolean setKeyValue(String key, String value) {
        RedisClusterConnection conn = redisConnectionFactory.getClusterConnection();
        return conn.set(key.getBytes(), value.getBytes());
    }

    public String getKeyValue(String key) {
        RedisClusterConnection conn = redisConnectionFactory.getClusterConnection();
        byte[] value = conn.get(key.getBytes());
        if(value != null) {
            return new String(value);
        }
        return null;
    }
}

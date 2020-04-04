package top.kwseeker.redis.boot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class AppConfig {

    @Autowired
    ClusterConfigurationProperties clusterProperties;

    @Bean
    public RedisConnectionFactory connectionFactory() {
        RedisClusterConfiguration rcc = new RedisClusterConfiguration(clusterProperties.getNodes());
        //配置客户端连接池(虽然redis-server服务端有IO多路复用，但是连接句柄是没法通过IO多路复用优化的，只能客户端方面限制连接数量)
        JedisPoolConfig jpc = new JedisPoolConfig();
        //TODO: 连接池配置
        return new JedisConnectionFactory(rcc, jpc);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //TODO: 这里还有很多配置选项，可以根据业务需要定制
        //如：序列化器等
        //redisTemplate.setDefaultSerializer();
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}


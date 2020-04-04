package top.kwseeker.redis.boot.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 使用RedisTemplate进行操作
 */
@Component
public class TmplOperator {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void setKeyValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getKeyValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setKeyHash(String key, Map<?, ?> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }
}

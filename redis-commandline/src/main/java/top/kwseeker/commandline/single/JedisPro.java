package top.kwseeker.commandline.single;

import redis.clients.jedis.Jedis;

public class JedisPro {

    public static void lpush(Jedis jedis, String key, String... strings) {
        if(jedis == null) {
            return;
        }
        for(String str : strings) {
            jedis.lpush(key, str);
        }
    }
}

package top.kwseeker.redis.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.JedisCluster;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
        JedisCluster jedisCluster = (JedisCluster) context.getBean("jedisCluster");

        System.out.println(jedisCluster.get("clusterTestKey"));
    }
}

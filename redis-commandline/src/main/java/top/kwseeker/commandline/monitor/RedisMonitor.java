package top.kwseeker.commandline.monitor;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisMonitor;

/**
 * 监控客户端操作监视器，详细看　JedisMonitor 机制。
 */
public class RedisMonitor {

    public static void main(String[] args) {
        MonitorTask monitorTask = new MonitorTask(new Jedis("localhost", 6379));
        new Thread(monitorTask).start();
    }

    static class MonitorTask implements Runnable {

        private Jedis jedis;

        public MonitorTask(Jedis jedis) {
            this.jedis = jedis;
        }

        @Override
        public void run() {
            //观察者模式，客户端发送命令请求后，redis-server会将命令信息发送给监视器链表里的每个监视器
            jedis.monitor(new JedisMonitor() {
                @Override
                public void onCommand(String command) {
                    //这里只是将命令信息打印出来
                    System.out.println(command);
                }
            });
        }
    }
}

package top.kwseeker.redis.usage;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPubSub;

public class Subscriber {

    private static class SubTask implements Runnable {
        private String pubChannel;

        public SubTask(String pubChannel) {
            this.pubChannel = pubChannel;
        }

        @Override
        public void run() {
            JedisCluster jedisCluster = new JedisCluster(RedisClusterConfig.hostAndPorts);

            jedisCluster.subscribe(new JedisPubSub() {  //相当于订阅监听器
                @Override
                public void onMessage(String channel, String message) {
                    System.out.println(Thread.currentThread().getName()+"-接收到消息:channel=" + channel + ",message=" + message);
                    //接收到exit消息后取消订阅退出
                    if ("exit".equals(message)) {
                        unsubscribe(pubChannel);
                    }
                }
            }, pubChannel);
        }
    }

    public static void main(String[] args) {
        String pubChannelKey = "pub:queue";
        //两个消息订阅者线程
        new Thread(new SubTask(pubChannelKey)).start();
        new Thread(new SubTask(pubChannelKey)).start();
    }
}

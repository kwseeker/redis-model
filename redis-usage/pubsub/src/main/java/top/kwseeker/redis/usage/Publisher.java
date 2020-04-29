package top.kwseeker.redis.usage;

import redis.clients.jedis.JedisCluster;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 实现发布者
 * 顺便测试下多连接并发情况下命令读写是穿插进行的
 */
public class Publisher {

    static class PubTask implements Runnable {
        private String tag;
        private String pubChannel;  //其实就是个Key
        private CyclicBarrier cyclicBarrier;
        public PubTask(String tag, String pubChannel, CyclicBarrier cyclicBarrier) {
            this.tag = tag;
            this.pubChannel = pubChannel;
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            try {
                JedisCluster jedisCluster = new JedisCluster(RedisClusterConfig.hostAndPorts);
                cyclicBarrier.await();
                for (int i = 0; i < 200; i++) {
                    jedisCluster.publish(pubChannel, tag + i);
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //３个发布者，每个发200条消息，由于数据比较少，需要CyclicBarrier控制同时进行发送才能看到效果
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
        String pubChannelKey = "pub:queue";
        new Thread(new PubTask("PublisherA:", pubChannelKey, cyclicBarrier)).start();
        new Thread(new PubTask("PublisherB:", pubChannelKey, cyclicBarrier)).start();
        new Thread(new PubTask("PublisherC:", pubChannelKey, cyclicBarrier)).start();

        Thread.sleep(10000);
        JedisCluster jedisCluster = new JedisCluster(RedisClusterConfig.hostAndPorts);
        jedisCluster.publish(pubChannelKey, "exit");
    }
}

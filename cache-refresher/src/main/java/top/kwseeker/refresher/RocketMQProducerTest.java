package top.kwseeker.refresher;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.Date;
import java.util.List;

public class RocketMQProducerTest {

    public static final String NAME_SRV_ADDR = "127.0.0.1:9876";
    public static final String PRODUCER_NAME = "refresher-test-producer";
    public static final String TOPIC_NAME = "refresher-test-topic";
    public static final String CONSUMER_NAME = "refresher-test-consumer";

    public static void main(String[] args) throws Exception {
        Thread producer = new Thread(() -> {
            try {
                producerStart();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.printf("Producer Done.%n");
        });
        Thread consumer = new Thread(() -> {
            try {
                consumerStart(producer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.printf("Consumer Done.%n");
        });

        consumer.start();
        producer.start();

        producer.join();
        consumer.join();
    }

    private static void producerStart() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_NAME);
        try {
            //设置注册中心
            producer.setNamesrvAddr(NAME_SRV_ADDR);
            //注册Producer、路由信息等等
            producer.start();
            System.out.printf("Producer Started.%n");

            for (int i = 0; i < 10; i++) {
                //创建消息实体，指定topic
                //TAGS: 为消息设置Tag标签，用于实现消息过滤; 可以通过多次调用setTags()指定多个标签
                Message msg = new Message(TOPIC_NAME, "TagA", ("Hello RocketMQ ! " + new Date().toString()).getBytes(RemotingHelper.DEFAULT_CHARSET) );
                SendResult sendResult = producer.send(msg);
                System.out.printf("send result: %s%n", sendResult);
                Thread.sleep(5000);
            }
        } finally {
            producer.shutdown();
        }
    }

    private static void consumerStart(Thread producer) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_NAME);
        consumer.setNamesrvAddr(NAME_SRV_ADDR);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        consumer.subscribe(TOPIC_NAME, "*");
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages,
                                                            ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), messages);
                for (MessageExt me : messages) {
                    System.out.println("message: " + new String(me.getBody()));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.printf("Consumer Started.%n");

        producer.join();
    }
}

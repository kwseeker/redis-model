package top.kwseeker.redis.usage;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * 测试Redis事务
 * 一个线程开启事务，修改某个key的值，另一个线程在它执行事务时修改key的值。
 */
public class TransactionTest {

    public static void main(String[] args) throws InterruptedException {
        String key = "txKey";

        Jedis cli = new Jedis("localhost", 6379);
        cli.set(key, "0");

        Thread txThread = new Thread(()->{
            try {
                Jedis txCli = new Jedis("localhost", 6379);
                txCli.watch(key);
                Transaction tx = txCli.multi();
                Thread.sleep(200);
                System.out.println("111");
                tx.set(key, "111");
                List<Object> list = tx.exec();
                //list中记录事务中每个命令的返回值，如果事务失败者返回空的list,但不是null
                System.out.println("result:" + list);
                txCli.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        txThread.start();

        Thread.sleep(100);
        System.out.println("222");
        cli.set(key, "222");
        txThread.join();
        cli.close();
        System.out.println("out");
    }
}

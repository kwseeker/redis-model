package top.kwseeker.redis.usage;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Redis实现乐观锁
 * 至于为何Redis是单线程的还需要加锁控制并发的原因和单核跑多线程是相同的原因。
 * 单线程的服务器处理多个连接，多个连接的任务（多个子步骤）操作同一个key，执行过程中多个连接的子步骤可能是穿插着揉合在一起。
 * 某个连接操作key的中间态时很可能被另一个连接某个子步骤修改。
 */
public class OptimisticLock {

    public static void main(String[] args) {
        String optimisticLock = "optimistic:lock";

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        Jedis jedis = new Jedis("localhost", 6379);
        //设置乐观锁初始值
        jedis.set(optimisticLock, "0");
        jedis.close();

        //假设模拟1000个用户秒杀20件商品的场景，使用乐观锁控制
        //注意这个秒杀并不是先到先得的
        for(int i=0; i<1000; i++) {
            executorService.execute(()->{
                Jedis jedisCli = new Jedis("localhost", 6379);
                jedisCli.watch(optimisticLock);
                int lockValue = Integer.valueOf(jedisCli.get(optimisticLock));
                String userInfo = UUID.randomUUID().toString();

                if(lockValue < 20) {
                    Transaction tx = jedisCli.multi();
                    tx.incr(optimisticLock);
                    List list = tx.exec();
                    if(list != null && list.size() > 0) {
                        System.out.println("用户: " + userInfo + "　秒杀成功，当前成功人数" + (lockValue + 1));
                    } else {
                        System.out.println("用户：" + userInfo + ", 秒杀失败");
                    }
                } else {
                    System.out.println("已经有20人秒杀成功,秒杀结束");
                }
                jedisCli.close();
            });
        }
        executorService.shutdown();
    }

}

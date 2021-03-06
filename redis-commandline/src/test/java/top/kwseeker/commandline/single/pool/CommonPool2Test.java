package top.kwseeker.commandline.single.pool;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试StringBuffer的对象池
 */
public class CommonPool2Test {

    /**
     * 默认是没有打开泄漏检测的
     */
    @Test
    public void testCommonPool2() {
        //使用默认的配置
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        //创建对象工厂
        PooledObjectFactory<StringBuffer> factory = new StringBufferFactory();
        //创建对象池
        ObjectPool<StringBuffer> pool = new GenericObjectPool<>(factory, config);

        StringReader in = new StringReader("abcdefg");
        StringBuffer buffer = null;
        try {
            //从对象池取对象
            buffer = pool.borrowObject();
            for (int c = in.read(); c != -1 ; c = in.read()) {
                buffer.append(c);
            }
            System.out.println(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                if(buffer != null) {
                    //返还对象
                    pool.returnObject(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCommonPool2WithEviction() {
        //使用默认的配置
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(6);   //8
        config.setMinIdle(2);   //0
        config.setBlockWhenExhausted(true);
        config.setMaxWaitMillis(3000);
        config.setJmxEnabled(true);     //打开监控
        //创建对象工厂
        PooledObjectFactory<StringBuffer> factory = new StringBufferFactory();
        //创建对象池
        GenericObjectPool<StringBuffer> pool = new GenericObjectPool<>(factory, config);
        //设置泄漏检测回收配置
        AbandonedConfig abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandonedOnMaintenance(true);
        abandonedConfig.setRemoveAbandonedOnBorrow(true);
        abandonedConfig.setRemoveAbandonedTimeout(5);      //设置泄漏检测基准时间，超过这个时间认为对象泄漏
        pool.setAbandonedConfig(abandonedConfig);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Task(pool));
            threads.add(thread);
            thread.start();
        }

        Thread monitor = new Thread(new MonitorTask(pool));
        monitor.start();

        try {
            for(Thread thread : threads) {
                thread.join();
            }
            monitor.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class MonitorTask implements Runnable {
        private GenericObjectPool<StringBuffer> pool;

        public MonitorTask(GenericObjectPool<StringBuffer> pool) {
            this.pool = pool;
        }

        @Override
        public void run() {
            //每200ms打印一次
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println(pool.getNumActive() + "\t"
                            + pool.getNumIdle() + "\t"
                            + pool.getNumWaiters() + "\t"
                            + pool.getBorrowedCount() + "\t"
                            + pool.getCreatedCount() + "\t"
                            + pool.getDestroyedByBorrowValidationCount() + "\t"
                            + pool.getDestroyedByEvictorCount() + "\t"
                            + pool.getDestroyedCount() + "\t"
                            + pool.getReturnedCount());
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                //e.printStackTrace();
                //return;
            }
        }
    }

    static class Task implements Runnable {
        private ObjectPool<StringBuffer> pool;
        private static AtomicInteger count = new AtomicInteger(1);

        Task(ObjectPool<StringBuffer> pool) {
            this.pool = pool;
        }

        @Override
        public void run() {
            StringBuffer buffer = null;
            try {
                //从对象池取对象
                buffer = pool.borrowObject();
                buffer.append("Hello world!");
                System.out.println(buffer.toString());
                int countTmp = count.getAndAdd(1);
                Thread.sleep(countTmp * 1000);   // 1、2、3、4 ...
                System.out.println(countTmp);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(buffer != null) {
                        //返还对象
                        pool.returnObject(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

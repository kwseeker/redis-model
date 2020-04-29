package top.kwseeker.redis.bloomfilter;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * 使用布隆过滤器解决缓存击穿的问题
 * 原理：多个Hash函数＋位数组
 * 错误率：查看位数组长度m与最终插入数据量n即hash函数个数k取值与错误率的表（m与n在同一个数量级，即 １< m/n <10）
 *  并不能保证所有hash函数算出来的位置上都是１的数据一定存在于缓存中, 即有可能出现漏网之鱼，针对漏网之鱼可以在查询数据库后（数据为null）,将其值设置为null存入redis缓存。
 *
 * 推荐使用Redis setbit getbit方法实现自己的布隆过滤器（redis已经有了bloomfilter插件），然后将布隆过滤器的数据交由Redis存储，单点问题和数据一致性通过集群配置解决。
 * 若使用Guava的话在微服务场景一个服务多个实例的场景下可能涉及单点问题和数据一致性必须解决的难题。
 *
 * 插件：https://github.com/RedisBloom/RedisBloom
 */
public class BloomFilterExample {

    public static void main(String[] args) {
        //1)当用户查询redis缓存发现缓存未命中，正确查询出结果后，缓存到redis; 然后同时将key求hash更新BloomFilter
        //  假设估计业务会插入10000条数据，错误率为0.01，自动计算对应的m和ｋ。
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 10000, 0.01);
        //  插入key
        //for(;;) {
            bloomFilter.put("00001");
            bloomFilter.put("001");
        //}

        //2)骇客使用不存在的key请求有两种情况：
        //2.1) 查询BloomFilter显示key不存在直接返回key不正确
        //2.2) key因为BloomFilter的错误率，本不存在于redis缓存，但是没有拦截住，这时经过mysql查询没有值，将null作为这个key的值存入redis缓存
        //  查询redis缓存之前获取请求key值判断是否存在
        String requestKey = "001";
        if(!bloomFilter.mightContain(requestKey)) {
            System.out.println("key 不存在");
            return;
        } else {
            //查询redis获取缓存数据
            System.out.println("value: ...");
        }
    }
}

package top.kwseeker.redis.usage;

import redis.clients.jedis.HostAndPort;

import java.util.HashSet;

public class RedisClusterConfig {

    public static HashSet<HostAndPort> hostAndPorts = new HashSet<>(6);
    static {
        hostAndPorts.add(new HostAndPort("10.0.0.2", 7000));
        hostAndPorts.add(new HostAndPort("10.0.0.3", 7001));
        hostAndPorts.add(new HostAndPort("10.0.0.4", 7002));
        hostAndPorts.add(new HostAndPort("10.0.0.5", 7003));
        hostAndPorts.add(new HostAndPort("10.0.0.6", 7004));
        hostAndPorts.add(new HostAndPort("10.0.0.7", 7005));
    }
}

package top.kwseeker.commandline.cluster;

import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;

public class NodesConfig {

    public static final Set<HostAndPort> nodes = new HashSet<>();

    static {
        nodes.add(new HostAndPort("localhost", 7000));
        nodes.add(new HostAndPort("localhost", 7001));
        nodes.add(new HostAndPort("localhost", 7002));
        nodes.add(new HostAndPort("localhost", 7003));
        nodes.add(new HostAndPort("localhost", 7004));
        nodes.add(new HostAndPort("localhost", 7005));
    }

}

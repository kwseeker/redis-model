package top.kwseeker.commandline.cluster;

import redis.clients.jedis.JedisCluster;


public class ClusterClient {

    public JedisCluster getClient() {
        return new JedisCluster(NodesConfig.nodes);
    }
}

package top.kwseeker.tests;

import redis.clients.jedis.Connection;
import redis.clients.jedis.Protocol;

public class ConnectionSetting {
    public static Connection connector;
    public static String host = "47.106.107.139";
    public static int port = Protocol.DEFAULT_PORT;
    public static int connectTimeout = 5000;              //连接超时时间，ms
    public static int timeout = Protocol.DEFAULT_TIMEOUT; //读写超时时间，ms
    public static int connectIdleTime = 2;                //连接空闲连接保持时间，s
    public static String password = "112358";
    public static int dbIndex = 0;                        //默认连接0号数据库

    //Shard
    public static int port2 = 6378;

}

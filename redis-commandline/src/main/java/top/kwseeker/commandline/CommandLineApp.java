package top.kwseeker.commandline;

import redis.clients.jedis.Jedis;
import top.kwseeker.commandline.constant.ConfigParam;

public class CommandLineApp {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");   //创建连接本地Redis服务的客户端
        jedis.auth(ConfigParam.PASSWORD);
        assert ("PONG".equals(jedis.ping())):"Redis connect failed..";     //开发时开启断言诊断Run/Debug Configurations VM options: -ea
        jedis.close();
    }
}

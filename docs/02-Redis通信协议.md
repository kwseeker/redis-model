# Redis 通信协议

+ redis-cli 是怎么与 redis-server 建立通信的？

+ 通信协议是怎么定义的？

  redis-cli 发一个命令，字符串传输编解码流程。



## 从 redis-cli 开始调试

在 redis-cli.c main() 方法加断点，看它都做了什么。


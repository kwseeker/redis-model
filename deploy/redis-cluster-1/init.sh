# redis-cluster 中还有问题启动异常，找了个替代方案
# 这是人家配置好的，按下面步骤操作即可快速搭建起　redis cluster。
# 里面重新build了６个镜像但是没有必要，TODO: 后期简化下，里面配置很简单
# 参考：
# https://itsmetommy.com/2018/05/24/docker-compose-redis-cluster/
# https://github.com/itsmetommy/docker-redis-cluster

git clone https://github.com/itsmetommy/docker-redis-cluster.git

cd docker-redis-cluster

docker-compose up --build -d

docker exec -it redis-1 redis-cli -p 7000 --cluster create 10.0.0.2:7000 10.0.0.3:7001 \
    10.0.0.4:7002 10.0.0.5:7003 10.0.0.6:7004 10.0.0.7:7005 \
    --cluster-replicas 1

# 测试

docker exec -it redis-1 redis-cli -c -p 7000

#redis> cluster nodes
#redis> cluster slots
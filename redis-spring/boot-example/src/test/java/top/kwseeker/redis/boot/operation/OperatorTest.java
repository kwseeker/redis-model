package top.kwseeker.redis.boot.operation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.kwseeker.redis.boot.config.ClusterConfigurationProperties;

import java.util.List;

@SpringBootTest
class OperatorTest {

    @Autowired
    private ClusterConfigurationProperties clusterProperties;
    @Autowired
    private Operator operator;

    @Test
    void printNodes() {
        List<String> nodes = clusterProperties.getNodes();
        for (String node : nodes) {
            System.out.println(node);
        }
    }

    @Test
    void testSetKeyValue() {
        operator.setKeyValue("clusterTestKey", "clusterTestValue");
    }

    @Test
    void testGetKeyValue() {
        System.out.println(operator.getKeyValue("clusterTestKey"));
    }
}
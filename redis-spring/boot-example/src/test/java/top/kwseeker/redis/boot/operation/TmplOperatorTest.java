package top.kwseeker.redis.boot.operation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TmplOperatorTest {

    @Autowired
    private TmplOperator operator;

    @Test
    void testSetKeyHash() {
        HashMap<String, String> person = new HashMap<>();
        person.put("name", "lee");
        person.put("job", "programmer");
        operator.setKeyHash("kwseeker", person);
    }

    @Test
    void testGetKeyValue() {
        System.out.println(operator.getKeyValue("clusterTestKey"));
    }
}
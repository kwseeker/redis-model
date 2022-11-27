package top.kwseeker.redis.theory.datastructure;

import org.junit.Test;

public class ZSkipListTest {

    @Test
    public void test() {
        ZSkipList zSkipList = new ZSkipList();
        zSkipList.insert(2, "B");
        zSkipList.insert(8, "H");
        zSkipList.insert(6, "F");
        zSkipList.insert(1, "A");
        zSkipList.insert(4, "D");
        zSkipList.insert(9, "I");
        zSkipList.insert(5, "E");
        zSkipList.insert(7, "G");
        zSkipList.print();

        zSkipList.insert(3, "C");
        zSkipList.print();
    }
}
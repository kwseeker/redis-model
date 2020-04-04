package top.kwseeker.redis.theory.lru;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 模拟　Redis LRU(least recent use) 实现
 * LinkedHashMap链表按访问顺序排序天然支持LRU的移位操作
 */
public class Lru {

    private int capacity;
    private LinkedHashMap<String, String> map;

    public Lru(int capacity) {
        this.capacity = capacity;
        //LinkedHashMap为何如此契合LRU的场景？难道是专门为LRU设计的数据结构？
        map = new LinkedHashMap<>(capacity, 0.75f, true);   //accessOrder=true,链表按访问顺序排序
    }

    public static void main(String[] args) {
        Lru lru = new Lru(10);
        for (int i = 1; i <= 10; i++) {
            lru.add(String.valueOf(i), "a"+i);
        }
        lru.printList();

        lru.add("5", "b1");
        lru.get("8");
        lru.printList();

        lru.del(3);
        lru.printList();
    }

    /**
     * 数据插入，插入头部(对应tail)
     */
    public void add(String key, String value) {
        if(map.size() == capacity) {
            //先从队尾删除第一个元素
            Map.Entry<String, String> first = map.entrySet().iterator().next();
            map.remove(first);
        }
        map.put(key, value);    //key如果已经存在会
    }

    /**
     * 数据查询，并将数据重新放在队列头部
     */
    public String get(String key) {
        return map.get(key);    //LinkedHashMap get() 会将节点移动到队尾
    }

    /**
     * 删除数据，只需要从尾部(head)删除数据即可。
     */
    public void del(int count) {
        for (int i = 0; i < count; i++) {
            //这里能不能优化？还有一种方法获取第一个元素：通过反射获取head,但感觉也不太好，实际应用的话估计需要extend重新封装下
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            if(iterator.hasNext()) {
                Map.Entry<String, String> item = iterator.next();
                map.remove(item.getKey(), item.getValue());
            }
        }
    }

    public void printList() {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        System.out.println("===========================");
        while (iterator.hasNext()) {
            Map.Entry<String, String> item = iterator.next();
            System.out.println("Key: " + item.getKey() + ", Value: " + item.getValue());
        }
    }
}

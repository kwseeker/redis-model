package top.kwseeker.redis.theory.datastructure;

/**
 * 参考Redis源码，用Java重新实现的跳表
 * 暂不支持泛型，value String
 * <p>
 * header节点不保存数据和分数，只是记录每层按正序排列的第一个节点
 * 最底层包含所有数据节点（按正序排列，span值记录当前节点在该层是第几个节点），
 */
public class ZSkipList {
    //跳表最大层数
    private static final int ZSKIPLIST_MAXLEVEL = 32;
    //private static final float ZSKIPLIST_P = 0.25F;
    private static final float ZSKIPLIST_P = 0.5F;

    //头节点
    private ZSkipListNode header;
    //尾节点
    private ZSkipListNode tail;
    //节点个数
    private long length;
    //记录元素节点层数的最大值
    private int level;

    public ZSkipList() {
        this.level = 1;
        this.length = 0;
        //头节点初始化，32个forward指针，初始指向全部为nulL
        this.header = new ZSkipListNode(ZSKIPLIST_MAXLEVEL, null, 0);
        for (int i = 0; i < ZSKIPLIST_MAXLEVEL; i++) {
            this.header.levels[i].forward = null;
            this.header.levels[i].span = 0;
        }
        this.header.backward = null;
        this.tail = null;
    }

    /**
     * insert
     * ZADD key [NX | XX] [GT | LT] [CH] [INCR] score member [score member ...]
     */
    ZSkipListNode insert(double score, String ele) {
        //rank 记录新元素节点在每一层的前面节点到header节点的跨度
        int[] rank = new int[ZSKIPLIST_MAXLEVEL];
        //新元素节点插入后需要更新forward指针的节点
        ZSkipListNode[] update = new ZSkipListNode[ZSKIPLIST_MAXLEVEL];

        //1 先定位到插入新节点的位置，定位过程查的一些数据（rank、update）记录下来后面要用
        ZSkipListNode node = header;
        for (int i = level - 1; i >= 0; i--) {
            rank[i] = i == (level - 1) ? 0 : rank[i + 1];
            System.out.println("rank[" + i + "]=" + rank[i]);
            while (node.levels[i] != null && node.levels[i].forward != null &&
                    (node.levels[i].forward.score < score ||
                            (node.levels[i].forward.score == score && node.levels[i].forward.ele.compareTo(ele) < 0))) {
                rank[i] += node.levels[i].span;
                node = node.levels[i].forward;
            }
            update[i] = node;   //新节点需要插入到 update[0] 指向节点的后面
        }

        //2 生成level
        int nodeLevel = randomLevel();
        System.out.println("nodeLevel=" + nodeLevel);
        if (nodeLevel > level) {
            for (int i = level; i < nodeLevel; i++) {
                rank[i] = 0;
                update[i] = header;
                update[i].levels[i].span = length;
            }
            level = nodeLevel;
        }

        //3 新建节点并插入
        node = new ZSkipListNode(nodeLevel, ele, score);
        for (int i = 0; i < nodeLevel; i++) {   //更新每一层的forward链表和span值
            node.levels[i].forward = update[i].levels[i].forward;
            update[i].levels[i].forward = node;

            System.out.println("rank[0]=" + rank[0] + ", rank[i]=" + rank[i]);
            node.levels[i].span = update[i].levels[i].span - (rank[0] - rank[i]);
            update[i].levels[i].span = rank[0] - rank[i] + 1;
        }

        for (int i = nodeLevel; i < level; i++) {
            update[i].levels[i].span++;
        }

        //4 更新backward链表，tail指向最后一个数据节点
        node.backward = update[0] == header ? null : update[0];
        if (node.levels[0].forward != null) {
            node.levels[0].forward.backward = node;
        } else {
            tail = node;
        }
        //5 更新节点个数
        length++;

        return node;
    }

    /**
     * delete
     */
    //int delete(double score, String ele) {
    //
    //}

    /**
     * updateScore
     */
    //int updateScore(double curScore, String ele, double newScore) {
    //
    //}

    private int randomLevel() {
        int level = 1;
        while (Math.random() < ZSKIPLIST_P)  //之前的版本记得是0.5
            level += 1;
        return Math.min(level, ZSKIPLIST_MAXLEVEL);
    }

    public void print() {
        ZSkipListNode node = this.header;
        while(node != null) {
            //System.out.println(node.ele+"|"+ node.score + "\t\t|bw:" + ((node.backward == null) ? "nu" : node.backward.ele) + "\t\t" + levelsStr(node));
            System.out.printf("%-5s | %-8f | bw: %-5s %s%n", node.ele, node.score, (node.backward == null) ? "null" : node.backward.ele, levelsStr(node));
            node = node.levels[0].forward;
        }
    }

    private String levelsStr(ZSkipListNode node) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < node.levels.length; i++) {
            if (node.levels[i].forward == null) {
                break;
            }
            sb.append("\t|").append(i).append(":").append(node.levels[i].span).append(":").append(node.levels[i].forward.ele);
        }
        return sb.toString();
    }

    static class ZSkipListNode {
        /**
         * 元素value
         */
        String ele;
        /**
         * 元素score
         */
        Double score;
        ZSkipListNode backward;
        ZSkipListLevel[] levels;

        public ZSkipListNode() {
            this.levels = new ZSkipListLevel[ZSKIPLIST_MAXLEVEL];
            for (int i = 0; i < ZSKIPLIST_MAXLEVEL; i++) {
                this.levels[i] = new ZSkipListLevel();
            }
        }

        public ZSkipListNode(int nodeLevel, String ele, double score) {
            this.ele = ele;
            this.score = score;
            this.levels = new ZSkipListLevel[nodeLevel];
            for (int i = 0; i < nodeLevel; i++) {
                this.levels[i] = new ZSkipListLevel();
            }
        }
    }

    static class ZSkipListLevel {
        ZSkipListNode forward;
        long span;  //记录当前指针跨越了多少节点，用于计算元素排名
    }
}

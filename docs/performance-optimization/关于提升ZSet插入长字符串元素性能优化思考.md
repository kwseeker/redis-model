# 关于提升ZSet插入长字符串元素性能优化思考

ZSet在榜单类需求中应用广泛，榜单的每条数据可能是长字符串（一般是对象序列化得到），通过ZADD命令插入单条数据，由于ZSet不能存储重复的值，势必会进行值比较（先找到相同hash值的元素数组，新插入数据和数组中已存在的值进行等值比较[具体过程还要看看详细实现原理测试下]），而长字符串等值比较是比较耗时的，但是应该可以通过控制序列化字段顺序（比如：FastJson的@JSONField的ordinal字段），将值差异较大的字段放在序列化字符串的前端，提升值比较的效率。

> 不过一般Hash冲突的概率很小，可能效果并不明显。
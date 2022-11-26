# JDBC
1、[JDBC mysql反序列化](https://xz.aliyun.com/t/8159)（没想着写笔记....后面心心师傅说这个利用方式有很多 于是又回来看 这个就略过了）

2、JDBC PostgreSQL（三种利用方式 构造函数为1个String的类加载 ssl（未复现成功 跟前一种方式应该相同） 写文件（需要其他组件例如log4j））

3、[JDBC H2 Database](https://blog.csdn.net/mole_exp/article/details/124243446)就没有深入分析了

4、[SQLite](https://paper.seebug.org/1832/#sqlite) 远程读取信息然后将其反序列化（也就是知道原理而已）

5、[Apache Derby](https://paper.seebug.org/1832/#apache-derby) (调试始终进不去 直接exec下了个断点看 就是读取socket中的信息 并且将其反序列化) 

# Java原生序列化与反序列化

先看这位[师傅的文章](https://blog.csdn.net/qq_36744284/article/details/89604011)

再看[李三师傅](https://redteam.today/2020/02/14/Java%E5%8E%9F%E7%94%9F%E5%BA%8F%E5%88%97%E5%8C%96%E4%B8%8E%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96%E4%BB%A3%E7%A0%81%E7%AE%80%E8%A6%81%E5%88%86%E6%9E%90/)

1. 为什么resolveClass方法可以防御反序列化？
   在反序列化时候必须调用resolveClass，而此时我们已经重写该方法，可以指定可以反序列化的类（如果不是可以抛出异常，结束程序！）

![image-20220919120602413](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209191206513.png)

![image-20220919120941246](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209191209298.png)

2、为什么在反序列化数据后面插入脏数据会不会影响反序列化
个人理解是，java在反序列化的时候是通过一定的标识符进行反序列化的。重点在标识符，李三师傅说按照字段长度，感觉有点理解不到。
后面又回去看了一下p神的文章，利用map形式注入脏数据是因为该map实现了java.io.Serializable接口并且能存储任意对象。而p神的文章是因为在反序列化的时候jdk处理会将TC_RESET忽略！（这是在开头加上脏数据），（在后面加上脏数据，这里不是很能理解）就是因为首个结构是 object ，处理完后反序列化就结束了， blockdata 根本没有处理，也就不会抛出异常了。

3、为什么就java反序列化使用而言是反序列化类的readObject开始？
在readSerialData的时候，首先会判断是否有readObject()，如果有会通过反射进行执行！
![image-20220919121741988](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209191217051.png)


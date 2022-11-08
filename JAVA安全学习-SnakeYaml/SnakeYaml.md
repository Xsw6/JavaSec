## SnakeYaml

[nice_0e3](https://www.cnblogs.com/nice0e3/p/14514882.html#%E6%BC%8F%E6%B4%9E%E5%A4%8D%E7%8E%B0)先看的这位师傅的文章！
看完之后，其实发现其与Fastjson真的很类似。反序列化时会调用：

- 构造方法
- setter方法

yaml反序列化时可以通过`!!`+全类名指定反序列化的类，反序列化过程中会实例化该类。

## 分析

跟进`yaml.load()`

![image-20220914100641578](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209141006652.png)

跟进
![image-20220914100858426](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209141008498.png)

![image-20220914100936062](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209141009163.png)

碰到return 就跟进，来到这里！![image-20220914101038242](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209141010304.png)

来到Constructor#getConstructor，跟进getClassForNode（其返回了一个class对象）

![image-20220914101345704](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209141013764.png)

接着向下执行会执行到Constructor#ConstructMapping将其newInstance。(`这里会调用其构造方法`)
![image-20220914102805918](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209141028969.png)

跟进，在其中发现其调用property.set()(`这里调用其set方法`)
![image-20220914102910392](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209141029434.png)

![image-20220914103753619](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209141037713.png)

这里自然可以想到Fastjson的调用setter方法触发的jndi。试试？

![](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209141042653.png)

## SPI

![image-20220914190239265](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209141902385.png)

## ScriptEngineManager

这里有个坑！在运用
（https://github.com/artsploit/yaml-payload/）这位师傅的payload时，记得要匹配java的版本！！！
不然一直会报错误以为是payload的问题。其实往下看报错会出现`Caused by: java.lang.UnsupportedClassVersionError: artsploit/AwesomeScriptEn`。也就是java的版本问题！

然后其实在后续的利用过程中注意的地方是：
为什么一定要在META-INF/services/目录下？

![image-20220915154116991](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209151541125.png)
在META-INF/services目录下的配置文件寻找实现类的类名
![image-20220915154704757](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209151547892.png)

最后通过class.forname加载，但是不会执行静态代码块的内容！这里为flase！！
![image-20220915154806203](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209151548267.png)
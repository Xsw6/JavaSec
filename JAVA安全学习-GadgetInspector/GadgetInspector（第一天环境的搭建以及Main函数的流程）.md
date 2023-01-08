# GadgetInspector（第一天环境的搭建以及Main函数的流程）

## 环境搭建

https://github.com/JackOfMostTrades/gadgetinspector

生成的链子在gadget-chains.txt中

### 碰到得问题

1、如何在idea中运行？（参数配置 --config jserial jar\war包路径）【需要通过阅读代码 下面也会做相应解释】
![image-20230108202544167](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301082025488.png)

2、JDK-x86无法设置超过4G得最大GC

3、还有些零零散散的问题.... 不重要

## 阅读代码

寻找整个程序的入口点：gadgetinspector.GadgetInspector#main()，同时我也是按照主函数的执行顺序进行分析代码的。（自己的学习路线是：先把main函数的一个整体框架看懂，遇到不会的地方，知道这个地方的大体作用就行，如果阅读这篇文章的师傅，读代码能力很nice可以直接一次性阅读完......哈哈哈哈不过高手会看我的文章吗?__？，感觉自己文章没啥水平，只是方便上手）

### 第一部分

归纳总结为：
1、解析传入的参数

##### 参数的解释

**--config 可运行指定的链子作者这里写的是所有的链子都为一个名字**
**--resume 将不会删除已经读取的一些相应文件，提高效率**

2、读取jar包到临时文件（并且jvm程序结束后会将其删除）

3、获取jar包类加载器

### MethodDiscovery()干了什么？

首先会进入discover()函数中调用classResourceEnumerator#getAllClasses()来到gadgetinspector.ClassResourceEnumerator#getRuntimeClasses()

![image-20230108205128358](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301082051591.png)

![image-20230108205912541](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301082059701.png)

继续调用gadgetinspector.MethodDiscovery#save()进行保存相关文件。
![image-20230108210123034](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301082101168.png)

### PassthroughDiscovery干了什么？

分析参数能影响到返回值的方法，并收集存储

### CallGraphDiscovery干了什么？

记录调用者caller方法和被调用者target方法的参数关联

### SourceDiscovery干了什么？

入口方法的搜索，只有具备某种特征的入口才会被标记收集

### GadgetChainDiscovery 干了什么？

整合以上数据，并通过判断调用链的最末端slink特征，从而判断出可利用的gadget chain。

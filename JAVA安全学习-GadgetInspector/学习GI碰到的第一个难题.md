# 学习GI碰到的第一个难题

首先感谢`心心师傅`、`su18师傅`、`任哥`的的帮助！

为什么我无法挖掘到如下Demo完整的链子？
![image-20230115123905920](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301151239058.png)

极限状态化（`我的`）：
sink点：
![image-20230115125014951](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301151250000.png)

结果：

![image-20230115123956274](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301151239305.png)

![image-20230115124101660](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301151241692.png)

好吧重学.
嗯这里再附上找到的（别人的成果...）
![image-20230115124714000](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301151247059.png)

## 问题可能出在的原因

1、MethodDiscovery (可能压根没有找到方法)

2、PassthroughDiscovery (方法之间的调用可能从一个方法走不到一个方法)

3、CallGraphDiscovery （参数传递）【这里没有理十分清楚】

也是这里发现的破案点：GI再进行参数污染的时候，无法处理无参函数！
![img](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301160040831.png)

4、getSourceDiscovery（source没找到...很显然找到了）

5、GadgetChainDiscovery(寻找的时候)

## 解决问题

## MethodDiscovery

逻辑不难理解：最后的结果就是将rt.jar和知道jar包中的class全部添加到result中，然后通过ASM进行观察记录每个类的详细信息`members`存放Filed，类中的方法存放再`discoveredMethods`中，类信息存放再`discoveredClasses`中。
后续将`discoveredClasses`存放到Class.dat文件，`discoveredMethods`存放到Method.dat.

两个的格式：
Class.dat:

```
类名(例：java/lang/String) 父类 接口A,接口B,接口C 是否接口 字段1!字段1access!字段1类型!字段2!字段2access!字段1类型
```

Method.dat:

```
类名 方法名 方法描述 是否静态方法
```

那么解决问题：

1、需要看看Class.dat文件有没有JndiSerialize、JdbcRowSetImpl、这两个类（想着至少要走到connect方法吧）。

2、需要看看Method.dat文件有没有setDataSourceName、getDatabaseMetaData、connect方法
![image-20230115130732523](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301151307590.png)

可见问题并不出现在这里。

同时这里还有后面的逻辑：会找到所有class的父类、超类以及接口类。存放在inheritanceMap.dat中。
格式为：

```
类名 父类或超类或接口类1 父类或超类或接口类2 父类或超类或接口类3 ...
```

## PassthroughDiscovery 

MethodCallDiscoveryClassVisitor作用：

`会处理所有的的CLass`，这里拿处理某个A类进行举例
这里可以debug一下：
![image-20230115134627460](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301151346519.png)

然后再理解一下流程。

第一步：挨个读取A类中所有的方法
第二步：在都到A类中第一个方法的时候会对方法中的方法继续处理（MethodCallDiscoveryMethodVisitor函数实现）
第三步：再MethodCallDiscoveryMethodVisitor中会将A类下的第一个方法中的所有方法保存再calledMethod中

最后`methodCalls`结果为：

```java
{{sourceClass,sourceMethod}:[{targetClass,targetMethod}]}

{{JndiSerialize,JndiTrue}:[{com/sun/rowset/JdbcRowSetImpl,<init>},{com/sun/rowset/JdbcRowSetImpl,setDataSourceName},{com/sun/rowset/JdbcRowSetImpl,getDatabaseMetaData}]}

//readObject也如上面类似。
```

我们可以发现JndiSerialize中每个方法中的所有方法都是被记录下来的。问题不出在这一步。

继续向下执行：逆拓扑排序部分
还是拿Demo说事：
![image-20230115144108433](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301151441546.png)

首先会获取[{com/sun/rowset/JdbcRowSetImpl,<init>},{com/sun/rowset/JdbcRowSetImpl,setDataSourceName},{com/sun/rowset/JdbcRowSetImpl,getDatabaseMetaData}]

{JndiSerialize,JndiTrue}方法压入栈。
相当于最后的栈结果变为：

```
...这里全是jdbcRowSet.getDatabaseMetaData()方法中所调用的方法
jdbcRowSet.getDatabaseMetaData()
...这里全是jdbcRowSet.setDataSourceName方法中所调用的方法
jdbcRowSet.setDataSourceName
...这里全是JdbcRowSetImpl#inti()方法中所调用的方法中所调用的方法
...这里全是JdbcRowSetImpl#inti()方法中所调用的方法
JdbcRowSetImpl#inti()
JndiSerialize#JndiTrue
```

理论上是这样，但是可能已经存在链子已经有了该方法的调用，已经将其存放到visitedNodes中了就会return（`换句话说有些方法已经被压入栈了，就跳过了压栈的步骤`）

同时理解一下：

1. dfsStack用于在在逆拓扑时候不会形成环
2. visitedNodes在一条调用链出现重合的时候，不会造成重复的排序
3. sortedMethods最终逆拓扑排序出来的结果集合

这里我调试的结果是：Demo再看`node.getClassReference().getName().equals("JndiSerialize")&&node.name.equals("JndiTrue")`只有两个被压入栈了，其他早再之前就被压入栈了。
![image-20230115155105139](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301151551215.png)

最后再处理Demo中的readObject方法，逻辑没区别。
![image-20230115160135095](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301151601161.png)

其实到这里就已经把连通的链子都存放到sortedMethods中了。

个人感觉问题应该出在这里？逻辑是这样：

如果A()->B()，C()->B()，再访问A方法的时候已经加入了，再访问C方法的时候发现一整条链子中有B方法，就不加入B方法。那么怎么确定C、B方法是连通的？要确定C、B连通就要确定A-C是连通的。这里显然没法解决这个问题。也许我理解错了。（`后面回来补的：其实我这个想法是错误的，因为它类似一个大的抽象二叉树，可以通过遍历遍历到每一个节点，只要其能够走通！`）
图就是这么个图：

![image-20230115172800617](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301151728684.png)

先继续向下走，会探索每个方法的入参是否会影响返回值。并且保存在passthrough.data中格式为

```
类名 方法名 方法描述 能污染返回值的参数索引1,能污染返回值的参数索引2,能污染返回值的参数索引3...
```

## CallGraphDiscovery

记录调用者caller方法和被调用者target方法的参数关联。

## GadgetChainDiscovery

再最后搜索的时候我发现其实再JndiTrue中没有找到能够调用getDatabaseMetaData方法，所以自然也就走不到lookup方法。

![image-20230115211220083](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301152112211.png)

同时我们也可以去寻找callgraph.dat文件中的数据，同样也发现找不到。那么知道了问题所在处就要去分析怎么生成的callgraph.dat。


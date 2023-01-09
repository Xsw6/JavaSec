# GadgetInspector-相关方法MethodDiscovery

## gadgetinspector.ClassReader#accept方法学习

一开始过于离谱...看到classRead中的过程去了....后来问了下心心师傅，他很疑惑我为什么会看这个？然后让我去看看asm api，于是我打开了以前的笔记.....
发现其实我们只要去关注gadgetinspector.MethodDiscovery#MethodDiscoveryClassVisitor()看看他干了什么？[`需要一些ASM的知识,虽然我已经忘得差不多了`]
但是不难看出，gi获取了哪些数据.
这里可以简单得介绍一下：
1、visit访问类的头部
2、visitField访问类属性
3、visitMethod访问类方法
并且会相应放入discoveredMethods和discoveredClasses中，最后通save方法，将value序列化放入class.dat和method.dat。

```java
 Map<ClassReference.Handle, ClassReference> classMap = new HashMap<>();
        for (ClassReference clazz : discoveredClasses) {
            classMap.put(clazz.getHandle(), clazz);
        } //.getHandle获取类名
```

接着进入gadgetinspector.data.InheritanceDeriver#derive

![image-20230109173928398](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301091739484.png)

这是一个循环的过程。
最后将类名作为key，所有父类、超类、接口类放入implicitInheritance
接着进入gadgetinspector.data.InheritanceMap#InheritanceMap()
就是一个反转的过程：

1. inheritanceMap 存放是子->父集合
2. subClassMap存放的是父->子集合


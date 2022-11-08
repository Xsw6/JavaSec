# Shiro为什么不能使用cc1

[学习1](https://xz.aliyun.com/t/7950#toc-4)

[学习2](https://lihuaiqiu.github.io/2020/10/06/shiro-1-2-4%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96%E5%8F%8A%E6%89%A9%E5%B1%95%E6%80%9D%E8%80%83/)

## Tomcat的WebappClassLoaderBase

![image-20220921100930617](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209211009784.png)

跟进当前线程的加载器`WebappClassLoaderBase#loadClass`

在以下几个地方进行加载过
1、`clazz = this.findLoadedClass0(name)`,检查当前要加载的类是否已经被WebappClassLoader加载过。
2、`clazz = JreCompat.isGraalAvailable() ? null : this.findLoadedClass(name);`从java.lang.ClassLoader类加载缓存检查当前类是否已经被加载过。
3、`clazz = javaseLoader.loadClass(name);`使用`ExtClassLoader`类加载器加载类。
4、![image-20220921102605909](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209211026029.png)

5、`clazz = this.findClass(name);`本地仓库中寻找该类。
6、`clazz = Class.forName(name, false, this.parent);`
以上都是按顺序进行 如果中间找到一个就结束。
当然报错，是因为上面没有一个能加载该类。来分析一下。

第五点中：会调用`clazz = this.findClassInternal(name);`跟入查看，发现获取不到对应的文件，导致返回null。
![image-20220921144440998](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209211457805.png)

第六点：采用的urlclassloader加载，但是没用...没有找到cc1对应的jar路径。


## 解决方法

直接将cc1链需要的jar包放入tomcat的lib目录下。
![image-20220921151305968](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209211513050.png)



然后还会从系统类加载器中加载，但是还是加载不到。(就没继续跟进了！)

所以才会有结论，不是不能加载数组只不过是不能加载除了urlclassloader的存在jar包中以外的数组！
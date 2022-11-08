# Log4j 一些特殊方式

https://xz.aliyun.com/t/10659、https://mp.weixin.qq.com/s/vAE89A5wKrc-YnvTr0qaNg、https://y4tacker.github.io/2022/07/06/year/2022/7/GoogleCTF2022-Log4j/（主要看到这个`Bundle`感觉很有意思）

##  Sys与Env

这一部分跳过。

## Bundle

![image-20221017120125620](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210171201695.png)

在同样的版本中，并没有发现其可以利用的Bundle。

最后又翻阅了很多资料，发现他们大多数都在运用Springboot环境（换上冲一波。）
果然发现多了几个。
![image-20221017123342519](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210171233572.png)

并且在本地利用输出版本号${java.version}也是不能成功的。但是在Springboot的环境下可以成功。

并且这里应该还存在一些其他问题。在结合Y4tacker的比赛来说，我手动设置了在`application`中设置flag{}却发现带不出来，但是如果只是简单的一个字符串确实可以。没有继续深究了。

![image-20221017124224904](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210171242016.png)

## 报错回显

​	4ra1n讲的很清楚啦~。
# Shiro

[简介](https://su18.org/post/shiro-1/#%E7%AE%80%E4%BB%8B)

## CVE-2010-3863

`影响版本`：shiro < 1.1.0 & JSecurity 0.9.x ， `JSecurity`，shiro的前名叫  JSecurity

`漏洞描述`：`Shiro`进行权限验证前未进行路径标准化，导致使用时可能绕过权限校验。出现点在`RequestURI`

### 分析

Shiro身份验证的流程主要发生在该类下。`org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver#getChain`
![image-20220904161814403](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209041618537.png)

跟进一下`getPathWithinApplication`![image-20220904162755585](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209041627666.png)

跟进一下`getRequestUri`![image-20220904163059704](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209041630755.png)

跟进一下`decodeAndCleanUriString`,其中截取了url中;后面的值，这里的案例中没有也就可以忽略。整个流程分析下来其实并没有去过滤什么/./或者/../这个路径。而在我们的配置中`filterMap.put("/secret.html", "authc,roles[admin]");`，如果没有权限进入是会跳转到登录页面的。
如果进行访问`/./secret.html`则成功绕过了。
![image-20220904164110811](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209041641933.png)

## CVE-2016-4437

`影响版本`：shiro 1.x < 1.2.5

`漏洞描述`：利用**硬编码**的密钥构造`rememberMe`参数，进行反序列化攻击`后面会分析`

## 分析

主要代码在`AbstractRememberMeManager#getRememberedPrincipals`
![](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209041935768.png)

`getRememberedSerializedIdentity`
![image-20220904201335702](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209042013792.png)

`convertBytesToPrincipals`

![image-20220904201753421](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209042017479.png)

![image-20220904201657236](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209042016297.png)

![image-20220904201830183](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209042018284.png)

`获取rememberMe值 -> Base64解密 -> AES解密 -> 调用readobject反序列化操作`这是其中的流程。这里跟网上大部分内容的结果不一样，能够弹出计算器！！！
这里切换版本试一下！(发现仍然可以。)(后面是发现此shiro，没有重写ObjectInputStream)

`p神的结论`：如果反序列化流中包含非 Java 自身的数组，则会出现无法加载类的错误。
解决办法：可以采用cc结合的方式，不用数组！也可以采用其他依赖cb链比如。
下面是bp的包中Cookie的构造。

```
Cookie: rememberMe=L7DFuRJNZpatXsS3y7GhFkyVKfbx8RnwerT5BLiYCc5sP+ODXkR3iMuc6p7yVbwkr43fmQ8X6NTzRrxUF1FVIC6jF5RPsSdO8KU4d+FXSLLajpSbi1L3k4oq9hxvel31W2L2y+TZDLLQ9abUEB7n1WRWKuwS0J7zohd+UgIvZ1wwsGlqmTm31i51mIrMucWPKthU1VIaiQpEcDWN0EfshZcFuNLg/NNkvKxSjaL3luCE/5rDbvWSzvwbXDSg0h6U2JBrnRET7mBlbU7Iafvrr2P0peygm13/njemyJu0zixpv8M9MZ1BpC1Nl6ZkkcOjX5wwOjYIwXW2dfLbAqRWvi6cH4xNW0CgJUqX6eNxYfwUhiCfOUGf+3oxK2X78W09pypSp0qJYU8DEnRfOiBXmv9c2UYPzqtTMrQRap4OVQzDBDvCaJaQUUzAFjtVB5wWBEMZc35s6Q5/WqTliGMHJ29iBMUE0OWL2/xPBp+NfB8BKubFlcLzK41TTeBjksy42wkfm/Eem8ePfNFcKZsDq0mnEShbfLyQ23UnJAFQsHYV9tng5GjMnpcX+sQeroZlYbYkB1erohSyv/XuOIa60CwBYiZCg81cE3pQisWHDK14BrQHpk0UZvKNrpPezf/OarT5IEz6t7/1jRUpqXH33iLFy7h7N9tz4yi/8nbQa46m8A9AwBx3C8Sb0Sjg9UZilNNw2/Eh/4jkSjcRlyL8z5GQfxbmzeb3Cmde0rgqvh4WJFV3HKCx+y4jdKB2vQdcBJKyeC8h51Z8wrJJs/LkdUUTVVUBWkcYZftqe38XdDcpPsS7xDtKRfe/EGHi9/dkNFyY5EX82ytAPddRHwb/z1oYxIoFVG2oxXtBxyLSjKWBMHfxCkMItFaogYD4gYb/TbJjMjnES9aY3SMqJOQSpdWsEguBqdKyuQlgQRdYDtMsvzhKA+oSuLx3BRvuCbHkM99aT3rc5saLtcY+vbGXIno9m6PCXaqNMRgPTj0WjhYZTY/QrvXef8ihIepYAv1c0KmPjh8heCJTlI5OwyBa5d3NNZ8FrbekZRUqC6q+BKhduznhxHVG/VvBLX321LhQ74/v0Oi+AnU3AYsm/huzpAITyJ0JovaHPG6PRS8URhbJRlFz9brmATRvV23846d1HGDUkjKONPuIP4qFfIA/u+DjmmiFM/EttlJi/5SKUgp+40C09cob9AJ3RJ97TGltnEOyM3lQLkMVXTQ9z6wxOG6Q+lzG3r6DC9tYc4mcfLK75tz2NWVTHFfIsHHqMq08AS1WYzWGyTY5JDmZy58caW4z2nQ7PrdOhrcKTVFFsjsODpvgiRjnOXD6wWYZGwXv/ciDiDhN/8Q67zb4DErafc4kSr8XsLOGxOIe6J1tK9AQRPCGG3Uvg71gIABRPMbeBzL3aOUcpWYEhl2ifEH+6DKz5dRmfC4YzIbgKLav2o1cQihpoQ5TLlO61Z4aYb1M1UrlQFX8xkBJ9BYeqo9AtFswKm8ZvcxRjuRoHnsBhH13w+ozkPGYGKxioJH4gBiQhrEZsZVvwL7qbyRhVR+6ev3l7j0bfmk8AfAbmuYmRPcX8/BFcx5cV6zq/Or+Ls1a1pdE4bBzqmPGIYr1om6cQJX02STHLl6VpvI51s0T8ZMAHJrUhxgrOYOAxksPk7/h5buptl5A1MCa80dNrNhzlhw3fY55ZdIQE2j04MxdBa+b0Ft4u323AsDY7neXoL/4
```

## CVE-2016-6802

`影响版本`：shiro < 1.3.2

`漏洞描述`：`Shiro`未对`ContextPath`做路径标准化导致权限绕过。出现点在`ContextPath`
环境没有搭建成功。

## CVE-2019-12422

`影响版本`：shiro < 1.4.2

`漏洞描述`：RememberMe Cookie 默认通过 AES-128-CBC 模式加密，这种加密方式容易受到
Padding Oracle Attack 攻击，攻击者利用有效的 RememberMe Cookie 作为前缀，
然后精心构造 RememberMe Cookie 值来实现反序列化漏洞攻击。

## 分析

[F1sh师傅讲的很清楚](https://f1sh.site/2017/08/04/%E5%88%9D%E5%AD%A6padding-oracle-attack/)
值得理解的是，0x01只能爆破第一位，然后这时候的middle第一位就是确定了！！然后循环爆破至第8位。

![img](https://skysec.top/images/enc.png)

![img](https://skysec.top/images/dec.png)

### CBC Byte-Flipping Attack

第一组的MediumValue确定了，第一组的Ciphertext也就确定了，第二组的明文也是可控的，那么第二组的MediumValue也可控，从而导致第二组的密文也可控。

问题流程如何判断我在爆破MediumValue是成功的呢？
再输入错误的Cookie: rememberMe=123时候会报错，产生出` rememberMe=deleteMe`。

理解了一下，并且再实战中shiro721是很难打出效果出来的。爆破时间量大！对方服务器可能会崩溃。

后续的几个版本都是路径的绕过问题

例如：

/admin/list  可以通过 /admin/list/进行绕过。

/audit/list 可以通过/audit//;aaaa/;...///////;/;/;awdwadwa/list

等等。。。

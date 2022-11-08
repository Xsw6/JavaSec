# Linux下Runtime.getRuntime.exec的相关问题

## 环境安装

虚拟机里直接下载idea即可，jdk版本无限制。

## 问题

在使用&&符号不能执行两条命令

![image-20220918205957147](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209182059245.png)

![image-20220918205927067](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209182059161.png)

## 分析

跟着这位[师傅](https://xz.aliyun.com/t/7046)学习！

## 总结

这位师傅一共给了两个解决方案：

1、采用数组的形式

```java
String[] command = { "/bin/sh", "-c", "echo 2333 2333 2333 && echo 2333 2333 2333" };
```

2、base64编码

[编码地址](https://x.hacking8.com/?post=293)

通过代码分析得知，`Runtime.getRuntime.exec()`在执行命令时，会以`\t \n \r \f` 将字符串分割成数组，而如果是数组就不会进行分割！最终会将`数组[0]`作为命令，其他数组作为数组[0]的参数。
然后师傅提出想法

```java
 String cmd = "/bin/bash -c 'echo 2333 && echo 2333'";
```

但是在最终处理参数的情况下字符串模式下执行的参数变成了 `-c\x00'echo\x002333\x00&&\x00echo\x002333'` ，对比数组模式 `-c\x00"echo 2333 && echo 23333"`
原因出在这里。
![image-20220918220356247](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209182203349.png)

看到这里我觉得可能是\x00的问题，但是转头一想这里都是\x00。

最后也是通过师傅的两张图片理解了原因！（`同为数组形式`）（没去调试jvm底层~~）
![image-20220918221846295](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209182218337.png)

![image-20220918221849707](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209182218762.png)

然后看了下三梦师傅的评论：
![image-20220918223302493](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209182233576.png)

那假如说传入这样一个参数呢？

```java
String[] command = {"/bin/sh", "-c", "echo 2333 && echo 2333","&&","echo 456"};
```

![image-20220918224520403](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209182245480.png)

`那么其实可以知道java底层只会处理-c后面的第一个" "中的内容！`
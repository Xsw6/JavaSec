# 有意思的JDK反射（影响版本>jdk9）

[学习](https://www.cnblogs.com/apocelipes/p/13562345.html)
[学习](https://www.cnblogs.com/6b7b5fc3/p/13697474.html)

![image-20221103110526035](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211031105170.png)



造成的直接原因：`其实这是JDK9中添加的新特性，即reflect不再可以访问non-public成员以及不可公开访问的class，原先这些访问控制虽然存在但是可以通过reflect绕过，从JDK9开始反射也将遵循访问控制的规则`。

那么问题出在那里呢？就是上面的直接原因，如何消除警告呢？
这里指出文章一个错误：`当使用命令执行java代码----不加文件后缀名java`

```java
java --illegal-access=warn TestReflect.java //错误
java --illegal-access=warn TestReflect //正确
```

## --illegal-access=permit

```
——illegal-access=permit 默认行为。允许对封装类型进行非法访问。当第一次尝试通过反射进行非法访问时会生成一个警告

——illegal-access=warn 与permit一样，但每次非法访问尝试时都会产生错误

——illegal-access=debug 同时显示非法访问尝试的堆栈跟踪。

——illegal-access=deny 不允许非法的访问尝试。这将是未来的默认行为
```

再jdk执行时候默认命令为：permit（显示警告信息）

如何消除警告？

```
--add-opens java.base/java.util=ALL-UNNAMED
```

![image-20221103111149699](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211031111744.png)
这样即使是访问no-public也是可以访问的了。

不过这个警告会产生什么影响呢？最后的结果仍然会运行....

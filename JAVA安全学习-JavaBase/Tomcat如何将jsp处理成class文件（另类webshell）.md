# Tomcat如何将jsp处理成class文件（另类webshell）

众所周知：java代码如何执行？需要先将Java文件变成class文件才能运行。

## Tomcat如何将jsp变为java文件

https://blog.csdn.net/cold___play/article/details/105143770 

## Tomcat如何将jsp变为java文件再变为class文件

https://y4tacker.github.io/2022/05/16/year/2022/5/JspWebShell%E6%96%B0%E5%A7%BF%E5%8A%BF%E8%A7%A3%E8%AF%BB/

在这里会正式开始将java文件处理成class
可以进入阅读一下逻辑（这里进入的是do while循环）：
当匹配`\r`或者`\n`的时候会跳入下一个字符。
![image-20221117215400282](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211172154378.png)

如果下一个字符匹配到`\t`、`\n`、`\f`、`\r`、`空格`
就会跳入下一个循环。也就是读取下一个字符了。
然后值得注意的是下一次循环。
![image-20221117220656004](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211172206056.png)

这里的逻辑就是匹配当前字符为`\`下一个字符为`u`时候就会进行Unicode
也就是符合这种逻辑 

```
\\u
```

但是后续出来的判断可能需要进行绕过。走到这里我脑袋有带你昏了....
回去看一下大师傅们用的payload是怎么样走的

```
<%
    Runtime.getRuntime().
    //\u000d\uabcdexec("calc");

%>
```

首先知道的是这也的代码是不会判断`checkIfUnicode`为true的。
再调试这个已知能绕过jsp编译的代码的过程中发现，其实就是判断了当前读入字符如果是`/`并且下一个字符为`/`或者`*`即可走入Y4tacker分析的那个过程。
问题是该如何控制输出的字符为`/`呢？这是一个问题....代码逻辑其实比较清楚但是实现起来可能要写一些脚本...`能力太差了再加上懒`...
当然在这里我的思路是发现可以通过控制解码后的值，为`/`再依次向下控制。（其实没必要，太复杂了）
大师傅们已经总结好了：Y4tacker文章中，最后为什么能过编译呢 我认为是开起了isJavadoc。
调试的我头昏眼花.....只是感觉自己一开始的方向有些问题。
当然再调试过程中我还发现可以无限制添加`\\\\\\\\\\\\\\\\\\`也能解析成功。相比jsp处理数据长度也是有限的吧？可能也会有些相关绕过。
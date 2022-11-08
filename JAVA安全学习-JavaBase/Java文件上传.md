# Java文件上传

面试问了很多次这东西，每次都回答对php比较熟悉（其实我一直感觉跟php差不多....但是不确定每次面试回答都这么回答）

https://zhuanlan.zhihu.com/p/431392700

`其实跟php没多大差别`。
值得注意的点：
1、%00截断只能再jdk7以及以下使用 yso中有一条链子就是利用这个特性。

2、**indexOf**和**lastIndexOf**的区别：

​		**int indexOf(String str):** 返回指定字符在字符串中第一次出现处的索引，如果此字符串中没有这样的字符，则返回 -1。

​		**public int lastIndexOf(String str):** 返回指定子字符串在此字符串中最右边出现处的索引，如果此字符串中没有这样的字符，则返回 -1

后端判断后缀名使用的是**filename.indexOf(".")，而不是filename.lastIndexOf(".")**，可通过双后缀名绕过检测，例如欲上传1.jsp，可将文件名改为1.jsp.jsp，这样后端获得的后缀名为.jsp.jsp

也就是说如果使用indexOf后端获取的后缀名为`.jsp.jsp`

其他的跟php真的没多大区别....

最好就直接白名单处理，多香啊~~
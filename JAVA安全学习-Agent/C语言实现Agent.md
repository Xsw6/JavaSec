# C语言实现Agent

其实我学到的东西很少，只是了解了有这么一个方法。

为什么学到的东西很少？因为我按照这篇文章：https://www.shuzhiduo.com/A/kvJ36qM9zg/

我尝试去用vs2022去生成动态链接库发现太困难了。

后面直接用的心心师傅：https://github.com/Firebasky/Java/blob/main/java%E6%97%A5%E5%B8%B8/c%E8%AF%AD%E8%A8%80%E8%83%BD%E5%AE%9E%E7%8E%B0agent%E5%90%97.md的代码。看了一下头文件，然后利用之前学jni的方法。直接手动生成了一个动态链接库。

## 我学到了什么？

类似premain：就需要在Agent_OnLoad函数中做些相应修改（也就是jvm启动的时候 `可利用vm参数:-agentpath:+dll的路径`）

类似agentmain：就需要在Agent_OnAttach函数中做些相应修改（启动之后）

而且如果在你的c代码中Agent_OnLoad和Agent_OnAttach函数只会执行二者中的一个。有Agent_OnLoad则不会执行Agent_OnAttach。


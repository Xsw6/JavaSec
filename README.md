# JavaSec

自己的学习记录，很菜
也当然学习路上肯定不止这些东西，很多都没做记录
哈哈哈哈文章有些乱，今年学的东西会好好做个类，争取打开让人看着有读的欲望....

## 2023的读书日记

2023/1/28 

[java类中serialversionuid作用](https://www.cnblogs.com/duanxz/p/3511695.html) [笔记](https://github.com/Xsw6/JavaSec/blob/main/JAVA%E5%AE%89%E5%85%A8%E5%AD%A6%E4%B9%A0-JavaBase/serialVersionUID.md)

[使用自定义ClassLoader解决反序列化serialVesionUID不一致问题](https://gv7.me/articles/2020/deserialization-of-serialvesionuid-conflicts-using-a-custom-classloader/) （掌握了反射、javassist、自定义Classloader其他的方法不大能理解）但是怎么说呢？感觉都挺麻烦...如果jar包一多手动修改的也多，跟替换jar包写poc耗费时间差别真的很大吗？ 学到一个思路吧

2023/1/30

[Weblogic如何识别T3、IIOP、HTTP](https://github.com/Xsw6/JavaSec/blob/main/JAVA%E5%AE%89%E5%85%A8%E5%AD%A6%E4%B9%A0-Weblogic/Weblogic%E5%A6%82%E4%BD%95%E8%AF%86%E5%88%ABT3%E3%80%81IIOP%E3%80%81HTTP.md)

[Java Zip Slip漏洞案例分析及实战挖掘](https://xz.aliyun.com/t/12081#toc-1)(最后一个漏洞，可以通过./../的形式绕过)

2023/2/1 [查杀Java web filter型内存马](https://gv7.me/articles/2020/kill-java-web-filter-memshell/) (感觉实战中可行的应该是查看classloader加载、对应class是否存在、恶意代码，主要都是利用Java Agent获取到所有加载到内存中的class然后进行一系列的判断)

2023/2/2 [西湖论剑比赛] 签到Misc+WEBJAVA(考点Fastjson+toString触发方法) 晚上学习了一下nginx反向代理和负载均衡在weblogic中的影响

一眨眼就过去了20天了，先是没有好工作的机会（迷茫）。后跟女朋友出去散散心，回来又准备考试...

2023/2/21 

[对IO重新学习了一下](https://www.bilibili.com/video/BV1n3411Q7gi?p=44&vd_source=ffa29603994e597f1f8a2562b25bcd08)

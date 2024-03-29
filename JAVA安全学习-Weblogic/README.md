# Weblogic

记录学习Weblogic

[Weblogic XMLDecode](https://github.com/Xsw6/JavaSec/blob/main/JAVA%E5%AE%89%E5%85%A8%E5%AD%A6%E4%B9%A0-Weblogic/WeblogicXML.md)


2023/1/13 [CVE-2015-4582](https://github.com/Xsw6/JavaSec/blob/main/JAVA%E5%AE%89%E5%85%A8%E5%AD%A6%E4%B9%A0-Weblogic/CVE-2015-4582.md)(对T3协议的理解)

2023/1/17 [CVE-2016-0638](https://github.com/Xsw6/JavaSec/blob/main/JAVA%E5%AE%89%E5%85%A8%E5%AD%A6%E4%B9%A0-Weblogic/CVE-2016-0638.md)(Weblogic黑名单的作用点 以及绕过【利用反序列化过程中经过的readExternal进行绕过】)

2023/1/18 [CVE-2016-3510](https://github.com/zhzhdoai/Weblogic_Vuln/blob/master/Weblogic_Vuln/src/main/java/com/weblogcVul/CVE_2016_3510.java)（原理如上类似 利用的是weblogic.corba.utils.MarshalledObject）

2023/1/18 [CVE-2020-2555](主要是阅读文章了[Weblogic CVE-2020-2555 反序列化RCE EXP构造](https://y4er.com/posts/weblogic-cve-2020-2555/#%E6%BC%8F%E6%B4%9E%E5%88%86%E6%9E%90))(主要是利用了CC链那一套方法)

2023/1/18 [CVE-2020-2555（环境搭建失败）](https://github.com/Xsw6/JavaSec/blob/main/JAVA%E5%AE%89%E5%85%A8%E5%AD%A6%E4%B9%A0-Weblogic/CVE-2020-2555%EF%BC%88%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E5%A4%B1%E8%B4%A5%EF%BC%89.md)(感觉使用windows真的不适合用qax大佬的方法........)

2023/1/20 [CVE-2022-2555](https://github.com/Xsw6/JavaSec/blob/main/JAVA%E5%AE%89%E5%85%A8%E5%AD%A6%E4%B9%A0-Weblogic/CVE-2020-2555.md)[仔细寻找其实会有很多另类方法 大都都是类似CC链]

2023/1/20 [CVE-2017-3248](利用JRMP 进行绕过)（碎碎念 到这里已经有两种办法进行绕过resolveClass 1、非禁用类2、JRMP）[这里就直接上链接了https://tttang.com/archive/1785/#toc_t3jrmp]

2023/1/26 [CVE-2018-3191](https://github.com/Xsw6/JavaSec/blob/main/JAVA%E5%AE%89%E5%85%A8%E5%AD%A6%E4%B9%A0-Weblogic/CVE-2018-3191.md)(JNDI攻击)

2023/1/26 [CVE-2020-2551](https://xz.aliyun.com/t/7422#toc-8)(这里碰到许多问题 iiop得流程中间流程看不懂，但是从poc以及网上得阅读文章来看，感觉很类似rmi将恶意对象放入注册端，weblogic又从注册端反序列化，利用手法也是JNDI也并不是协议得问题)

2023/1/30 [Weblogic如何识别T3、IIOP、HTTP](https://github.com/Xsw6/JavaSec/blob/main/JAVA%E5%AE%89%E5%85%A8%E5%AD%A6%E4%B9%A0-Weblogic/Weblogic%E5%A6%82%E4%BD%95%E8%AF%86%E5%88%ABT3%E3%80%81IIOP%E3%80%81HTTP.md)

## 读过比较好的关于Weblogic得文章

2023/1/26 [判断Weblogic详细版本号的方法总结](https://mp.weixin.qq.com/s/z6q1sBYcHYgzvak98QQmeA)[用T3请求头、相关漏洞、特殊路径]

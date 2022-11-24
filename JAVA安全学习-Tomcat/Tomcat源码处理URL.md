# Tomcat源码处理URL

https://xz.aliyun.com/t/11871

两处关键点：
1、org.apache.catalina.connector.CoyoteAdapter#parsePathParameters (主要处理`;`,`/`)

```
/upload;test ==> /upload
/upload;test/ ==> /upload/
/upload;test;/ ==> /upload/
/upload;te;st/ ==> /upload/
/upload;te/st/ ==> /upload/st/
/upload;te/s;t/ ==> /upload/s/
/upload;te;s;t/ ==> /upload/
/upload;te;s/t/ ==> /upload/t/
```

2、org.apache.catalina.connector.CoyoteAdapter#normalize(主要处理`\\`和`../`)

```
/upload/../ ==> /
/upload/./ ==> /upload/
/./upload/ ==> /upload/
/../upload/ ==> /upload/
\upload/ ==> /upload/
\\upload/ ==> /upload/
```


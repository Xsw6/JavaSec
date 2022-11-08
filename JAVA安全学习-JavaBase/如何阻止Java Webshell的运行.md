# 如何阻止Java Webshell的运行

[n1nty](https://mp.weixin.qq.com/s?__biz=MzI5Nzc0OTkxOQ==&mid=2247483687&idx=1&sn=bba37d896553131446072c9c681eed51&chksm=ecb11dd7dbc694c1ddbfe646bd6aed26a8fc1aad1d1f1beb00e50df9c44bb4d71bb3472977b4&scene=126&&sessionid=1666923013#rd)

```
在 web.xml 文件中加下以下配置：

<jsp-config>

    <jsp-property-group>

      <url-pattern>*.jspx</url-pattern>

      <url-pattern>*.jsp</url-pattern>

       <scripting-invalid>true</scripting-invalid>

    </jsp-property-group>

  </jsp-config>
```

手动尝试了下，tomcat内存马就没有用了

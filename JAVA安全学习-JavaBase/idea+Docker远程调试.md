# idea+Docker远程调试

## 制作DockerFile

需要再dockerfile中添加如下语句，并且暴露端口。

![image-20221104143153801](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211041431908.png)

```
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD ./app.jar /app.jar
EXPOSE 18081
EXPOSE 8453
ENTRYPOINT ["java",  "-jar","-Djava.security.egd=file:/dev/./urandom","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8453","/app.jar"]
MAINTAINER LandGrey
```

## 建立docker环境

```
docker build -t springbootfile . 
```

## 启动环境

```
docker run -p 8453:8453 -p 4567:18081 springbootfile
```

再idea中放入源码，做好配置即可远程调试。
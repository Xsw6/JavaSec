# SPI机制实现rce

最近学了springboot 文件至rce，读三梦师傅文章的时候对spi很不理解。
找到了一个[项目](https://www.jianshu.com/p/32370d9b9046)。
在其中实际调试过程中会调用到`java.util.ServiceLoader#nextService`
![image-20221104140838224](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211041409551.png)

也就是说如果我们在存在任意文件上传的情况下，我们可以覆盖META-INF/services下的文件，如下情况：
![image-20221104141126873](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211041411949.png)

然后再将其编译，上传至
![image-20221104141204431](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211041412508.png)

导致其可以试先rce。
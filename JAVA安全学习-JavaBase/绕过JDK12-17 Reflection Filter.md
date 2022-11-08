# 绕过JDK12-17 Reflection Filter

[学习](http://paper.vulsee.com/KCon/2021/%E9%AB%98%E7%BA%A7%E6%94%BB%E9%98%B2%E6%BC%94%E7%BB%83%E4%B8%8B%E7%9A%84Webshell.pdf)

先来看看是个什么样的过滤器：
![image-20221105153704332](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211051537415.png)

那这个过滤器到底是怎么样使用的呢？

在使用`getDeclaredField`的时候会调用其`privateGetDeclaredFields(false)`方法，最后来到`Reflection.filterFields()`
![image-20221105155604855](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211051556969.png)

这里如果匹配到了fieldFilterMap中的值则会跳过第一个if判断，如果是获取到了`*`号那么则会进入filteredNames.contains(WILDCARD)，获得一个空对象。

![image-20221105160028401](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211051600428.png)

最后也就自然抛出异常。

## 解决办法

1、反射将其置空（失败）[关键字volatile](https://www.php.cn/java/base/468363.html)，private static应该是可以修改。但是Reflection.class也在该类下，反射这条路肯定走不通。

![image-20221105160246660](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211051602722.png)

2、[学习1](https://paper.seebug.org/1785/)

![image-20221105160756379](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211051607450.png)

```java
import sun.misc.Unsafe;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

public class bypass {
    private static Unsafe getUnsafe() {
        Unsafe unsafe = null;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        return unsafe;
    }
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[4096];
        int len=-1;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while ((len=inputStream.read(bytes))!=-1){
            byteArrayOutputStream.write(bytes,0,len);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public void bypassReflectionFilter()throws Exception{
        Unsafe unsafe = getUnsafe();
        Class reflectionClass=Class.forName("jdk.internal.reflect.Reflection");
        byte[] classBuffer = readInputStream(reflectionClass.getResourceAsStream("Reflection.class"));
        //定义一个类，但不让类加载器知道它。
        Class reflectionAnonymousClass = unsafe.defineAnonymousClass(reflectionClass,classBuffer,null);

//        String name = reflectionAnonymousClass.getName();
//        System.out.println(name);
        Field fieldFilterMapField=reflectionAnonymousClass.getDeclaredField("fieldFilterMap");
        //不需要
        //Field methodFilterMapField=reflectionAnonymousClass.getDeclaredField("methodFilterMap");

        if(fieldFilterMapField.getType().isAssignableFrom(HashMap.class)){
            unsafe.putObject(reflectionClass,unsafe.staticFieldOffset(fieldFilterMapField),new HashMap());
        }
        //if(methodFilterMapField.getType().isAssignableFrom(HashMap.class)){
        //  unsafe.putObject(reflectionClass,unsafe.staticFieldOffset(methodFilterMapField),new HashMap());
        //}
    }
    public static void main(String[] args) throws Exception{
        //绕过Java 反射过滤获取ClassLoader私有字段
//        Class<?> aClass = Class.forName("jdk.internal.reflect.Reflection");//在反射之前会报错
//        aClass.getDeclaredField("fieldFilterMap");
        new bypass().bypassReflectionFilter();
        Class<?> aClass = Class.forName("jdk.internal.reflect.Reflection");//在之后反射可以bypass
        aClass.getDeclaredField("fieldFilterMap");
    }
}
```


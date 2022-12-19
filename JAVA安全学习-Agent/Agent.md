# Agent

通过这张图片来说我们可以知道有两种方法实现agent，`第一种Java Agent` `第二种C语言 Agent`

![image-20221027172216225](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212191133459.webp)

## Java Agent

### premain

流程：https://xz.aliyun.com/t/9450#toc-2

注意点：
如何将一个java文件打包成jar包。（`目录选择方面值得注意`）

https://blog.csdn.net/qq_31914787/article/details/88064557

![image-20221219130035082](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212191300179.png)

**只能在启动时使用`-javaagent`参数指定**，可以看出来在安全方面用处并不是很大。

### agentmain

（图个方便吧亦或者图个流程类似）需要修改的地方。
在`META-INF/MANIFEST.MF`中需要新增两行，完整如下：

```
Manifest-Version: 1.0
Agent-Class: javaagent.AgentDemo
Can-Redefine-Classes: true
Can-Retransform-Classes: true

```

#### Instrumentation

https://xz.aliyun.com/t/9450#toc-5
个人认为值得注意的点：`addTransformer方法配置之后，后续的类加载都会被Transformer拦截。对于已经加载过的类，可以执行retransformClasses来重新触发这个Transformer的拦截。类加载的字节码被修改后，除非再次被retransform，否则不会恢复。`
所以我们可以创建如下类：

```java
package javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class DefineTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println("你运行完就该我运行了！！");
        return classfileBuffer;
    }
}

```

```java
package javaagent;

import java.lang.instrument.Instrumentation;

public class AgentDemo {
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        instrumentation.addTransformer(new DefineTransformer(), true);

    }
}

```

```java
package javaagent;

import com.sun.tools.attach.*;

import java.io.IOException;
import java.util.List;

public class AgentTest {
    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {

        System.out.println("main running");
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor vir : list) {
            System.out.println(vir.displayName());//打印JVM加载类名
            if (vir.displayName().endsWith("javaagent.AgentTest")){
                VirtualMachine attach = VirtualMachine.attach(vir.id());   //attach注入一个jvm id注入进去
                attach.loadAgent("F:\\Agent\\out\\artifacts\\Agent_jar2\\Agent.jar");//加载agent
                attach.detach();
            }
        }
    }
}

```

进行测试。
在jvm启动之后，我们捕获到了某个进程的id，就将agent.jar进行加载。这里也就说明要不要这个`if`判断都不重要（以及返回的结果是多个`你运行完就该我运行了！！`）。
做个小结：agentmain在jvm启动后进行类加载的时候会被addTransformer方法配置之后，后续的类加载都会被Transformer拦截。
目前我们已经能对其进行拦截了，并且能添加任意代码。
此时我们可以利用捕获某个类jvmid，对其代码进行修改。
流程：https://xz.aliyun.com/t/9450#toc-12

值得注意的是：`对于已经加载过的类，可以执行retransformClasses来重新触发这个Transformer的拦截`
所以这里需要进行如下的改动

```java
package javaagent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class AgentDemo {
    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws UnmodifiableClassException, ClassNotFoundException {
            Class[] classes = instrumentation.getAllLoadedClasses();
            for (Class aclass : classes){
                System.out.println(aclass);
            }
            instrumentation.addTransformer(new DefineTransformer(), true);
            instrumentation.retransformClasses(Class.forName("javaagent.hello"));
        }
}

```

### 总结

如何在实战中利用？(`首先要上传个文件.....也就是自己的恶意类Agent jar包`)
实战中我们只需要一个能加载此类的方法（多数都是利用`TemplatesImpl`）
https://y4er.com/posts/javaagent-tomcat-memshell/#%E5%A6%82%E4%BD%95%E9%80%9A%E8%BF%87%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96%E6%B3%A8%E5%85%A5

限制比较大.....可能这只是在rce之后的维持权限方面的作用了？





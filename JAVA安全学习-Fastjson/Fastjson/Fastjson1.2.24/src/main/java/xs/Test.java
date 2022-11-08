package xs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

public class Test {
    public static void main(String[] args) {
//        Person xs = new Person("xs", 22);
//        String s = JSON.toJSONString(xs); //会调用其类属性的getter方法  //{"age":22,"name":"xs"}
//        System.out.println(s);
//
//      String s1 = JSON.toJSONString(xs, SerializerFeature.WriteClassName);//SerializerFeature.WriteClassName是toJSONString设置的一个属性值，设置之后在序列化的时候会多写入一个@type
//      System.out.println(s1); //{"@type":"xs.Person","age":22,"name":"xs"}

//以上简单的介绍为对象转为字符串
//        String s = "{\"age\":22,\"name\":\"xs\"}";
//        System.out.println(JSON.parse(s));
//        System.out.println(JSON.parseObject(s));
//        System.out.println(JSON.parseObject(s,Person.class));

        String s = "{\"@type\":\"xs.Person\",\"age\":22,\"name\":\"xs\"}";
           System.out.println(JSON.parse(s));
//          System.out.println(JSON.parseObject(s));
//        System.out.println(JSON.parseObject(s,Person.class));
    }
}

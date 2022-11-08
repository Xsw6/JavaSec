package test;

import java.io.Serializable;
import java.util.Map;

public class User implements Serializable {
    private Map<String, Class<Object>> innerMap;

    public User() {
        System.out.println("调用无参构造方法");
    }

    public User(Map<String, Class<Object>> innerMap) {
        System.out.println("调用有参构造方法");
        this.innerMap = innerMap;
    }

    public Map<String, Class<Object>> getInnerMap() {
        System.out.println("调用getInnerMap");
        return innerMap;
    }

    public void setInnerMap(Map<String, Class<Object>> innerMap) {
        System.out.println("调用setInnerMap");
        this.innerMap = innerMap;
    }

    @Override
    public String toString() {
        return "User{" +
                "innerMap=" + innerMap +
                '}';
    }
}

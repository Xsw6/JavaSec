package xs;

import java.io.IOException;

public class Person {
    static{
        try {
            Runtime.getRuntime().exec("calc");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String name;
    private int age;

    public Person() {
        System.out.println("调用无参构造方法！！！");
    }

    public Person(String name, int age) {
        System.out.println("调用有参构造方法！！！");
        this.name = name;
        this.age = age;
    }

    public String getName() {
        System.out.println("调用getName方法");
        return name;
    }

    public void setName(String name) {
        System.out.println("调用setName方法");
        this.name = name;
    }

    public int getAge() {
        System.out.println("调用getAge方法");
        return age;
    }

    public void setAge(int age) {
        System.out.println("调用setAge方法");
        this.age = age;
    }
}

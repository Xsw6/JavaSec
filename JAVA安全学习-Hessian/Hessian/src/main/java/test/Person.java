package test;

import java.io.Serializable;

public class Person implements Serializable {
    private String name;
    private Integer age;


    public Person() {
        System.out.println("调用无参构造方法");
    }

    public Person(String name, Integer age) {
        System.out.println("调用有参构造方法");
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

    public Integer getAge() {
        System.out.println("调用getAge方法");
        return age;
    }

    public void setAge(Integer age) {
        System.out.println("调用setAge方法");
        this.age = age;
    }
}

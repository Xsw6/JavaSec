package xs;

import java.io.IOException;

public class Person {
    private String name;
    private int age;
    private String sex;


    public Person(String name, int age ) {
        this.name = name;
        this.age = age;
    }

    public Person(String name, int age, String sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }



    public String getName() {
        System.out.println("调用getter方法-name");
        return name;
    }

    public void setName(String name) {
        System.out.println("调用setter方法-name");
        this.name = name;
    }

    public int getAge() {
        System.out.println("调用getter方法-age");
        return age;
    }

    public void setAge(int age) {
        System.out.println("调用setter方法-age");
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }




//    public String getSex() throws IOException {
//        Runtime.getRuntime().exec("calc");
//        return sex;
//    }
//
//    public void setSex(String sex) {
//        this.sex = sex;
//    }
}

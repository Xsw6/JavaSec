package test;

import org.yaml.snakeyaml.Yaml;

public class Demo {
    public static void main(String[] args) {
        Person person = new Person();
        person.setName("xs");
        Yaml yaml = new Yaml();
        String dump = yaml.dump(person); //将对象转为yaml格式
        System.out.println(dump);
    }
}
package xs;

import org.yaml.snakeyaml.Yaml;

public class Demo {
    public static void main(String[] args) {
//        Person xs = new Person("xs", 22);
//        Yaml yaml = new Yaml();
//        String dump = yaml.dump(xs);
//        System.out.println(dump);//序列化

        Yaml yaml = new Yaml();
        Object load = yaml.load("!!xs.Person {age: 22, name: xs}");
    }
}

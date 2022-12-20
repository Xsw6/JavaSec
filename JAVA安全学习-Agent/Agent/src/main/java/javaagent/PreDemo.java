package javaagent;

import java.lang.instrument.Instrumentation;

public class PreDemo {
    public static void premain(String args, Instrumentation inst) throws Exception{
        for (int i = 0; i < 10; i++) {
            System.out.println("hello I`m premain agent!!!");
        }
    }
}

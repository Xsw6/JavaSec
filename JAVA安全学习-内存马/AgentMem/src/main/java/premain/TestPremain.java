package premain;

import java.lang.instrument.Instrumentation;

public class TestPremain {
    public static void premain(String args, Instrumentation inst) throws Exception{
        for (int i = 0; i < 3; i++) {
            System.out.println("premain agent!!!");
        }
    }
}
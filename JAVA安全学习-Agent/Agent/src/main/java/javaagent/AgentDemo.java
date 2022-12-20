package javaagent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class AgentDemo {
    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws UnmodifiableClassException, ClassNotFoundException {
            Class[] classes = instrumentation.getAllLoadedClasses();
            for (Class aclass : classes){
                System.out.println(aclass);
            }
            instrumentation.addTransformer(new DefineTransformer(), true);
            instrumentation.retransformClasses(Class.forName("javaagent.hello"));
        }
}

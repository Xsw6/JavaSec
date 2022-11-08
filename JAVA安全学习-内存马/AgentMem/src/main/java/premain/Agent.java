package premain;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String agentArgs, Instrumentation inst){
        System.out.println("agentArgs"+agentArgs);
        inst.addTransformer(new MyClassTransformer(),true);//调用addTransformer添加一个Transformer
    }
}
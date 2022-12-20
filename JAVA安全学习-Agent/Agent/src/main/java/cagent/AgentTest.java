package cagent;

import com.sun.tools.attach.*;

import java.io.IOException;
import java.util.List;

public class AgentTest {
    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {

        System.out.println("main running");
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor vir : list) {
            System.out.println(vir.id());
            System.out.println(vir.displayName());//打印JVM加载类名
            if (vir.displayName().endsWith("cagent.AgentTest")){
              System.out.println("哈哈哈"+vir.id());
                VirtualMachine attach = VirtualMachine.attach(vir.id());   //attach注入一个jvm id注入进去
//                attach.loadAgent("F:\\Agent\\out\\artifacts\\Agent_jar2\\Agent.jar");//加载agent
              attach.loadAgentPath("D:\\Java\\dlllibary\\agent.dll");
                attach.detach();
            }
        }
    }
}

package xs;
import com.sun.tools.attach.*;

import java.io.IOException;
import java.util.List;


public class AgentMain {


    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {

        System.out.println("main running");
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor vir : list) {
            System.out.println(vir.displayName());//打印JVM加载类名
            if (vir.displayName().endsWith("xs.AgentMain")){
                VirtualMachine attach = VirtualMachine.attach(vir.id());   //attach注入一个jvm id注入进去
                attach.loadAgent("F:\\AgentMem\\target\\AgentMem-1.0-SNAPSHOT.jar");//加载agent
                attach.detach();

            }
        }

    }
}
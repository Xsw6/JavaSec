package xs;

public class Demo {

    public static void main(String[] args) {
        try{
            java.io.File toolsPath = new java.io.File(System.getProperty("java.home").replace("jre","lib") + java.io.File.separator + "tools.jar");
            System.out.println(toolsPath.toURI().toURL());
            java.net.URL url = toolsPath.toURI().toURL();
            java.net.URLClassLoader classLoader = new java.net.URLClassLoader(new java.net.URL[]{url});
            Class<?> MyVirtualMachine = classLoader.loadClass("com.sun.tools.attach.VirtualMachine");
            Class<?> MyVirtualMachineDescriptor = classLoader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
            java.lang.reflect.Method listMethod = MyVirtualMachine.getDeclaredMethod("list",null);
            java.util.List<Object> list = (java.util.List<Object>) listMethod.invoke(MyVirtualMachine,null);

            System.out.println("Running JVM Start..");
            for(int i=0;i<list.size();i++){
                Object o = list.get(i);
                java.lang.reflect.Method displayName = MyVirtualMachineDescriptor.getDeclaredMethod("displayName",null);
                String name = (String) displayName.invoke(o,null);
                System.out.println(name);
                if (name.contains("xs.Demo")){
                    java.lang.reflect.Method getId = MyVirtualMachineDescriptor.getDeclaredMethod("id",null);
                    java.lang.String id = (java.lang.String) getId.invoke(o,null);
                    System.out.println("id >>> " + id);
                    java.lang.reflect.Method attach = MyVirtualMachine.getDeclaredMethod("attach",new Class[]{java.lang.String.class});
                    java.lang.Object vm = attach.invoke(o,id);
                    java.lang.reflect.Method loadAgent = MyVirtualMachine.getDeclaredMethod("loadAgent",new Class[]{java.lang.String.class});
                    java.lang.String path = "F:\\AgentMem\\target\\AgentMem-1.0-SNAPSHOT.jar";
                    loadAgent.invoke(vm,path);
                    java.lang.reflect.Method detach = MyVirtualMachine.getDeclaredMethod("detach",null);
                    detach.invoke(vm,null);
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
package javaagent;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class DefineTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            try {

                ClassPool cp = ClassPool.getDefault();
                if (classBeingRedefined != null) {
                    ClassClassPath ccp = new ClassClassPath(classBeingRedefined);
                    cp.insertClassPath(ccp);
                }
                CtClass ctClass = cp.get("javaagent.hello");
                CtMethod method = ctClass.getDeclaredMethod("hello");
                method.insertBefore("{System.out.println(\"hello transformer\");}");
                ctClass.writeFile("F:\\Agent\\src\\main\\java\\javaagent\\hello.class");

                return ctClass.toBytecode();
//            ClassPool cp = ClassPool.getDefault();
//            if (classBeingRedefined != null) {
//                ClassClassPath ccp = new ClassClassPath(classBeingRedefined);
//                cp.insertClassPath(ccp);
//            }
//                CtClass ctc = cp.get("javaagent.hello");
//                CtMethod method = ctc.getDeclaredMethod("hello");
//                String source = "{System.out.println(\"hello transformer\");}";
//                method.setBody(source);
//                byte[] bytes = ctc.toBytecode();
//                ctc.detach();
//                return bytes;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            } catch (NotFoundException | CannotCompileException | IOException e) {
                e.printStackTrace();
            }
//        System.out.println("在你结束之后运行！！！");
//        return classfileBuffer;
        return null;
    }
}

package javaagent;

import javassist.*;

import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get("javaagent.hello");
        CtMethod method = ctClass.getDeclaredMethod("hello");
        method.insertBefore("{System.out.println(\"hello transformer\");}");
        ctClass.writeFile("F:\\Agent\\src\\main\\java\\javaagent");
    }
}

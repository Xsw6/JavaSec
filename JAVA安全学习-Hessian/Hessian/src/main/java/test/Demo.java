package test;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.MapDeserializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws IOException {
        Person xs = new Person("xs", 22);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
        hessianOutput.writeObject(xs);

        System.out.println(byteArrayOutputStream.toString());

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        HessianInput hessianInput = new HessianInput(byteArrayInputStream);
        Object o = hessianInput.readObject();
        System.out.println(o);

    }
}

package test;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class Demo1 {
    public static void main(String[] args) throws IOException {
        Map innerMap = new HashMap<String, Class<Object>>();
        innerMap.put("1",ObjectInputStream.class);
        User user = new User(innerMap);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
        hessianOutput.writeObject(user);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        HessianInput hessianInput = new HessianInput(byteArrayInputStream);

        System.out.println(hessianInput.readObject());
    }
}

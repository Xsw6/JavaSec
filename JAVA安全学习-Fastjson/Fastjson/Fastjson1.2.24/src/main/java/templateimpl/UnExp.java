package templateimpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class UnExp {
    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream("F:\\Fastjson\\src\\main\\java\\templateimpl\\Exp.class"));
        byte[] bytes = new byte[1024];
        int len=0;
        while(-1!=(len=bufferedInputStream.read(bytes,0,1024))){
                byteArrayOutputStream.write(bytes,0,len);
        }
        for(int i=0;i<bytes.length;i++){
            System.out.print(bytes[i]+",");

        }
        System.out.println("\n");
        System.out.println(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));


        byte[] bytes1 = Files.readAllBytes(Paths.get("F:\\Fastjson\\src\\main\\java\\templateimpl\\Exp.class"));
        String s = Base64.getEncoder().encodeToString(bytes1);
        System.out.println(s);

    }
}

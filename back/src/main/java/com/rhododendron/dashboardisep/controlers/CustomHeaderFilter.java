package com.rhododendron.dashboardisep;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Created by JavaDeveloperZone on 16-12-2017.
 */
@Component
public class CustomHeaderFilter implements Filter {
    @Override
    public void destroy() {
        System.out.println("destroy filter. release our resources here if any");
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
            IOException,ServletException {
        HttpServletResponse httpServletResponse=(HttpServletResponse)response;
        httpServletResponse.setHeader("Access-Control-Expose-Headers","Authorization");
        chain.doFilter(request, response);      // continue execution of other filter chain.
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Init filter");
    }
}
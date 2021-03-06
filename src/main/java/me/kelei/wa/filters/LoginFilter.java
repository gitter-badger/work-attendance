package me.kelei.wa.filters;

import me.kelei.wa.utils.JYWaUtil;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 登录验证
 * Created by kelei on 2016/9/22.
 */
public class LoginFilter implements Filter {

    private String regPath = "/css/*|/image/*|/js/*|/fonts/*|/login/*";

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        HttpSession session = request.getSession();

        Object user = session.getAttribute("waUser");
        String logoutFlag = (String) session.getAttribute("logout");
        Pattern pattern = Pattern.compile(regPath);

        if(!pattern.matcher(request.getRequestURI()).find()){
            if(user == null){
                Cookie[] cookies = request.getCookies();
                String rememberme = null;
                String wapid = null;
                if(cookies != null && cookies.length > 0){
                    for(Cookie cookie : cookies){
                        if("wapid".equals(cookie.getName()))
                            wapid = cookie.getValue();
                        if("rememberme".equals(cookie.getName()))
                            rememberme = cookie.getValue();
                    }

                }

                response.setDateHeader("Expires",0);
                response.setHeader("Cache-Control","no-cache");
                response.setHeader("Pragma","no-cache");

                if("1".equals(rememberme) && !"1".equals(logoutFlag)){
                    request.setAttribute("wapid",wapid);
                    request.getRequestDispatcher("/rest/login/rememberMe").forward(request, response);
                }else{
                    session.setAttribute("logout", null);
                    request.getRequestDispatcher("/page/login.html").forward(request, response);
                    return;
                }
            }
        }


        filterChain.doFilter(request, response);
    }

    public void destroy() {

    }
}

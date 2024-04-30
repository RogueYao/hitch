package com.heima.commons.initial.filter;

import com.heima.commons.constant.HtichConstants;
import com.heima.commons.entity.SessionContext;
import com.heima.commons.helper.RedisSessionHelper;
import com.heima.modules.vo.AccountVO;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@WebFilter(urlPatterns = "/*", initParams = {
        @WebInitParam(name = "paramName", value = "paramValue")
})
@Component
public class TokenAuthFilter implements Filter {

    private final static Logger logger = LoggerFactory.getLogger(TokenAuthFilter.class);

    final List<String> path = Arrays.asList(
            "/account/api/login",
            "/account/api/register",
            "/payment/api/nofify"
    );

    @Autowired
    private RedisSessionHelper redisSessionHelper;

    @Override
    public void doFilter(ServletRequest servletRequest0, ServletResponse servletResponse0, FilterChain filterChain) throws IOException, ServletException {
        // 获取request和response，注意：不是HttpServletRequest及HttpServletResponse
        HttpServletRequest request = (HttpServletRequest) servletRequest0;
        HttpServletResponse response = (HttpServletResponse) servletResponse0;
        AccountVO vo = (AccountVO) request.getSession().getAttribute("user");
        logger.info("===> sessionToken : {} , request path : {}",vo,request.getServletPath());
        //验证放行路径
        if (path.contains(request.getServletPath())
                || !StringUtils.contains(request.getServletPath(),"/api/")) {
            // 认证通过放行
            filterChain.doFilter(servletRequest0,servletResponse0);
            return;
        }
        //非空判断
        if (vo == null) {
            // 响应未认证！
            printError(response);
            // 结束请求
            return ;

        }
        // 认证通过放行
        filterChain.doFilter(request,response);
    }

    private void printError(HttpServletResponse response){
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.println("Access Forbidden"); // 写入响应体
        out.flush(); // 刷新输出流
        out.close(); // 关闭输出流
    }

    public static class PathConfig {
        private String path;
        private String[] pathArray;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            if (StringUtils.isNotEmpty(path)) {
                pathArray = path.split(";");
            }
            this.path = path;
        }

        public String[] getPathArray() {
            return pathArray;
        }
    }


}
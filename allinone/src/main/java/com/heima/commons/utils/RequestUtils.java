package com.heima.commons.utils;

import com.heima.commons.constant.HtichConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    /**
     * 获取Request对象
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

    /**
     * 获取Head中的数据
     *
     * @param headerName
     * @return
     */
    public static String getRequestHeaderOrCookie(String headerName) {
        HttpServletRequest request = getRequest();
        String v = request.getHeader(headerName);
        if (StringUtils.isEmpty(v)){
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (headerName.equals(cookie.getName())){
                    v = cookie.getValue();
                    break;
                }
            }
        }
        return v;
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public static String getCurrentUserId() {
        return getRequestHeaderOrCookie(HtichConstants.HEADER_ACCOUNT_KEY);
    }

}

package com.heima.commons.utils;

import com.heima.commons.constant.HtichConstants;
import com.heima.modules.vo.AccountVO;
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
     * 获取当前用户信息
     *
     * @return
     */
    public static String getCurrentUserId() {
        HttpServletRequest request = getRequest();
        AccountVO vo = (AccountVO) request.getSession().getAttribute("user");
        if (vo == null) return null;
        return vo.getId();
    }

}

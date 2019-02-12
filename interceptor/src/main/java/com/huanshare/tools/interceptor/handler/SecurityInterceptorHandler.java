package com.huanshare.tools.interceptor.handler;

import com.huanshare.tools.interceptor.service.SecurityService;
import com.huanshare.tools.interceptor.service.SecurityService;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  2018/11/14.
 */
public class SecurityInterceptorHandler extends InterceptorHandler{
    private static Logger logger = Logger.getLogger(SecurityInterceptorHandler.class);

    private SecurityService securityService;

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public Boolean doHandler(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return securityService != null ? securityService.isLogin(request) : true;
    }
}

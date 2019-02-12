package com.huanshare.tools.interceptor.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  2018/11/14.
 */
public abstract class InterceptorHandler {

    //response redirect url
    private String redirectUrl;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public abstract Boolean doHandler(HttpServletRequest request, HttpServletResponse response, Object handler);

}

package com.huanshare.tools.interceptor;

import com.huanshare.tools.interceptor.handler.InterceptorHandler;
import com.huanshare.tools.utils.ListUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class HotPlugInterceptor extends HandlerInterceptorAdapter {

    private List<InterceptorHandler> handlers;

    public List<InterceptorHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<InterceptorHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(ListUtils.isNotEmpty(handlers)){
            for(InterceptorHandler interceptorHandler : handlers){
                if(!Boolean.TRUE.equals(interceptorHandler.doHandler(request, response, handler))){
                    response.sendRedirect(request.getContextPath() + interceptorHandler.getRedirectUrl());
                    return false;
                }
            }
        }
        return true;
    }
}

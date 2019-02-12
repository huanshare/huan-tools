package com.huanshare.tools.interceptor.handler;

import com.huanshare.tools.enums.TrueFalseEnum;
import com.huanshare.tools.interceptor.annotation.Permission;
import com.huanshare.tools.interceptor.model.PermissionModel;
import com.huanshare.tools.interceptor.service.PermissionService;
import com.huanshare.tools.interceptor.service.SecurityService;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.enums.TrueFalseEnum;
import com.huanshare.tools.interceptor.annotation.Permission;
import com.huanshare.tools.interceptor.model.PermissionModel;
import com.huanshare.tools.interceptor.service.PermissionService;
import com.huanshare.tools.interceptor.service.SecurityService;
import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  2018/11/14.
 */
public class PermissionInterceptorHandler extends InterceptorHandler{
    private static Logger logger = Logger.getLogger(PermissionInterceptorHandler.class);

    private String isOpen = String.valueOf(TrueFalseEnum.TRUE.getKey());
    private PermissionService permissionService;
    private SecurityService securityService;

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    @Override
    public Boolean doHandler(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(!String.valueOf(TrueFalseEnum.TRUE.getKey()).equals(isOpen)){
            return true;
        }

        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        Permission permission = ((HandlerMethod) handler).getMethod().getAnnotation(Permission.class);
        if(permission == null){
            return true;
        }
        String permissionCode = permission.code();
        if(StringUtils.isBlank(permissionCode)){
            return true;
        }

        String userKey = securityService != null ? securityService.getCookieValue(request) : null;
        if(StringUtils.isBlank(userKey)){
            return false;
        }

        //多个code用逗号分隔，只要匹配一个code，就说明有权限显示
        String[] strings = StringUtils.split(permissionCode, ",");
        boolean isExist = false;
        for(String str : strings){
            PermissionModel model = permissionService.getValue(userKey, permission.type(), str);
            if(model != null){
                isExist = true;break;
            }
        }
        if(!isExist){
            return false;
        }

        return true;
    }
}

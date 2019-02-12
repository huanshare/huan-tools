package com.huanshare.tools.interceptor.tag;

import com.huanshare.tools.interceptor.enums.PermissionTypeEnum;
import com.huanshare.tools.interceptor.model.PermissionModel;
import com.huanshare.tools.interceptor.service.PermissionService;
import com.huanshare.tools.interceptor.service.SecurityService;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.interceptor.enums.PermissionTypeEnum;
import com.huanshare.tools.interceptor.model.PermissionModel;
import com.huanshare.tools.interceptor.service.PermissionService;
import com.huanshare.tools.interceptor.service.SecurityService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 *  2018/11/15.
 */
public class PermissionTag extends BodyTagSupport {

    private PermissionService permissionService;
    private SecurityService securityService;
    private String code = "";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void doInitBody() throws JspException {
        super.doInitBody();
    }

    @Override
    public int doStartTag() throws JspException {
        ServletContext servletContext = pageContext.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        Object permissionServiceObj = webApplicationContext.getBean("permissionService");
        if(permissionServiceObj != null){
            permissionService = (PermissionService)permissionServiceObj;
        }
        Object securityServiceObj = webApplicationContext.getBean("securityService");
        if(securityServiceObj != null){
            securityService = (SecurityService)securityServiceObj;
        }

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        String userKey = securityService != null ? securityService.getCookieValue(request) : null;
        if(StringUtils.isBlank(userKey)){
            return SKIP_BODY;
        }

        //多个code用逗号分隔，只要匹配一个code，就说明有权限显示
        String[] strings = StringUtils.split(code, ",");
        boolean isExist = false;
        for(String str : strings){
            PermissionModel model = permissionService.getValue(userKey, PermissionTypeEnum.FUNCTION, str);
            if(model != null){
                isExist = true;break;
            }
        }
        if(!isExist){
            return SKIP_BODY;
        }

        return EVAL_BODY_INCLUDE;
    }
}

package com.huanshare.tools.mvc.aspect.params;

import java.util.Map;

/**
 *  2017/5/18.
 */
public abstract class AbstractParamsReturnHandler {
    //时间服务类
    private Object showTimeService;
    //时间方法
    private String showTimeServiceMethod;

    public Object getShowTimeService() {
        return showTimeService;
    }

    public void setShowTimeService(Object showTimeService) {
        this.showTimeService = showTimeService;
    }

    public String getShowTimeServiceMethod() {
        return showTimeServiceMethod;
    }

    public void setShowTimeServiceMethod(String showTimeServiceMethod) {
        this.showTimeServiceMethod = showTimeServiceMethod;
    }

    public abstract void doReturnHandler(Object returnObj, Map requestMap) throws Exception;

}

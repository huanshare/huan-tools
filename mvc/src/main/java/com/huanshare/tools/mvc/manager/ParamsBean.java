package com.huanshare.tools.mvc.manager;

import java.util.HashMap;
import java.util.Map;

/**
 *  2017/7/10.
 */
public class ParamsBean {
    private Map<String, Object> requestParamsMap = new HashMap<>();
    private Map<String, Object> extDefineParamsMap = new HashMap<>();

    public Map<String, Object> getRequestParamsMap() {
        return requestParamsMap;
    }

    public void setRequestParamsMap(Map<String, Object> requestParamsMap) {
        this.requestParamsMap = requestParamsMap;
    }

    public Map<String, Object> getExtDefineParamsMap() {
        return extDefineParamsMap;
    }

    public void setExtDefineParamsMap(Map<String, Object> extDefineParamsMap) {
        this.extDefineParamsMap = extDefineParamsMap;
    }

    public Map<String, Object> getAllRequestParamsMap() {
        Map<String, Object> allRequestParams = new HashMap<>();
        allRequestParams.putAll(requestParamsMap);
        allRequestParams.putAll(extDefineParamsMap);
        return allRequestParams;
    }
}

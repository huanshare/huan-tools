package com.huanshare.tools.mvc.manager;

import org.apache.log4j.Logger;

import java.util.Map;

/**
 *  2017/3/31.
 */
public class ParamsThreadLocalManager {

    private static Logger logger = Logger.getLogger(ParamsThreadLocalManager.class);
    private static ParamsThreadLocalManager manager = null;

    private ThreadLocal<ParamsBean> threadLocal = new ThreadLocal();

    private ParamsThreadLocalManager() {
    }

    public static synchronized ParamsThreadLocalManager getInstance() {
        if (manager == null) {
            manager = new ParamsThreadLocalManager();
        }

        return manager;
    }

    public void putParams(Map requestParamsMap) {
        putParams(requestParamsMap, null);
    }

    public void putParams(Map requestParamsMap, Map extDefineParamsMap) {
        ParamsBean params = new ParamsBean();
        if (requestParamsMap != null) {
            params.setRequestParamsMap(requestParamsMap);
        }
        if (extDefineParamsMap != null) {
            params.setExtDefineParamsMap(extDefineParamsMap);
        }
        threadLocal.set(params);
    }

    public ParamsBean getParams() {
        return threadLocal.get();
    }

    public void removeParams() {
        threadLocal.set(null);
    }

}

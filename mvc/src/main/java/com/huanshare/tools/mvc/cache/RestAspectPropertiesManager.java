package com.huanshare.tools.mvc.cache;

import com.huanshare.tools.mvc.model.RestAspectProperties;
import com.huanshare.tools.mvc.model.RestAspectProperties;

/**
 * service配置数据缓存
 */
public class RestAspectPropertiesManager {

    private static RestAspectPropertiesManager restAspectPropertiesManager = null;

    private RestAspectProperties restAspectProperties = new RestAspectProperties();

    private RestAspectPropertiesManager() {

    }

    public static synchronized RestAspectPropertiesManager getInstance() {
        if (restAspectPropertiesManager == null) {
            restAspectPropertiesManager = new RestAspectPropertiesManager();
        }
        return restAspectPropertiesManager;
    }

    public RestAspectProperties getProperties() {
        return restAspectProperties;
    }

    public void setProperties(RestAspectProperties properties) {
        this.restAspectProperties = properties;
    }

}

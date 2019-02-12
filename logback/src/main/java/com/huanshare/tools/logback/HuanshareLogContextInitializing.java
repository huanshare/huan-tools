package com.huanshare.tools.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

import com.huanshare.tools.cache.CacheDatabaseConfiguration;
import com.huanshare.tools.utils.CommonUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by huanshare on 2018/1/13.
 */
public class HuanshareLogContextInitializing implements InitializingBean{

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HuanshareLogContextInitializing.class);

    private CacheDatabaseConfiguration cacheDatabaseConfiguration;
    private Map properties;
    private String filePath;

    public void setCacheDatabaseConfiguration(CacheDatabaseConfiguration cacheDatabaseConfiguration) {
        this.cacheDatabaseConfiguration = cacheDatabaseConfiguration;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filePath);
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(loggerContext);
            loggerContext.reset();

            if(properties != null){
                Set set = properties.keySet();
                for(Iterator iterator = set.iterator();iterator.hasNext();){
                    String key = (String)iterator.next();
                    String value = (String)properties.get(key);
                    if(cacheDatabaseConfiguration != null){
                        loggerContext.putProperty(key, cacheDatabaseConfiguration.getString(value, ""));
                    }else{
                        loggerContext.putProperty(key, CommonUtils.getString(value));
                    }
                }
            }
            configurator.doConfigure(resourceAsStream);
        } catch (Exception e) {
            logger.error("init logBack xml error , " + e.getMessage(), e);
        }
    }

}

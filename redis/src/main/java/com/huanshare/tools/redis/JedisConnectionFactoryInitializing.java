package com.huanshare.tools.redis;

import com.huanshare.tools.cache.CacheDatabaseConfiguration;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.cache.CacheDatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * Created by junming.qi on 2018/2/25.
 */
public class JedisConnectionFactoryInitializing extends JedisConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisPoolConfigInitializing.class);

    private CacheDatabaseConfiguration cacheDatabaseConfiguration;
    private String hostNameConfigDBKey;
    private String portConfigDBKey;
    private String passwordConfigDBKey;

    @Override
    public void afterPropertiesSet() {
        setHostName(getDBConfigByKey(hostNameConfigDBKey, getHostName()));
        setPort(Integer.valueOf(getDBConfigByKey(portConfigDBKey, "6397")));
        setPassword(getDBConfigByKey(passwordConfigDBKey, getPassword()));
        super.afterPropertiesSet();
    }

    public String getDBConfigByKey(String key, String defaultValue) {
        if (StringUtils.isBlank(key) || cacheDatabaseConfiguration == null) {
            return defaultValue;
        }
        return cacheDatabaseConfiguration.getString(key, defaultValue);
    }


    public String getHostNameConfigDBKey() {
        return hostNameConfigDBKey;
    }

    public void setHostNameConfigDBKey(String hostNameConfigDBKey) {
        this.hostNameConfigDBKey = hostNameConfigDBKey;
    }

    public String getPortConfigDBKey() {
        return portConfigDBKey;
    }

    public void setPortConfigDBKey(String portConfigDBKey) {
        this.portConfigDBKey = portConfigDBKey;
    }

    public String getPasswordConfigDBKey() {
        return passwordConfigDBKey;
    }

    public void setPasswordConfigDBKey(String passwordConfigDBKey) {
        this.passwordConfigDBKey = passwordConfigDBKey;
    }

    public CacheDatabaseConfiguration getCacheDatabaseConfiguration() {
        return cacheDatabaseConfiguration;
    }

    public void setCacheDatabaseConfiguration(CacheDatabaseConfiguration cacheDatabaseConfiguration) {
        this.cacheDatabaseConfiguration = cacheDatabaseConfiguration;
    }


}

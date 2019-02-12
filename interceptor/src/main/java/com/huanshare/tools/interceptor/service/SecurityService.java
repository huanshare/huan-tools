package com.huanshare.tools.interceptor.service;

import com.huanshare.tools.interceptor.model.UserInfoModel;
import com.huanshare.tools.redis.RedisService;
import com.huanshare.tools.utils.AES128Utils;
import com.huanshare.tools.utils.CookieUtils;
import com.huanshare.tools.utils.JacksonUtils;
import com.huanshare.tools.utils.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 *  2018/11/14.
 */
public class SecurityService {
    private static Logger logger = Logger.getLogger(SecurityService.class);

    private RedisService redisService;

    private int cookieTimes = 1 * 24 * 60 * 60;
    private long redisTimes = 1 * 24 * 60 * 60;
    private String loginCookieKey;
    private String aes128CookieKey;
    private String redisLoginKey;


    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    public void setLoginCookieKey(String loginCookieKey) {
        this.loginCookieKey = loginCookieKey;
    }

    public void setRedisLoginKey(String redisLoginKey) {
        this.redisLoginKey = redisLoginKey;
    }

    public void setAes128CookieKey(String aes128CookieKey) {
        this.aes128CookieKey = aes128CookieKey;
    }

    public void setCookieTimes(int cookieTimes) {
        this.cookieTimes = cookieTimes;
    }

    public void setRedisTimes(long redisTimes) {
        this.redisTimes = redisTimes;
    }

    public void setRedisValue(String userKey, UserInfoModel userInfoModel) {
        if (StringUtils.isBlank(userKey) || redisService == null) {
            return;
        }

        ObjectMapper objectMapper = JacksonUtils.getInstance();
        try {
            putToRedis(userKey, objectMapper.writeValueAsString(userInfoModel));
        } catch (IOException e) {
            logger.error("PermissionService setValue userPermissionHashMap error", e);
            return;
        }
    }

    private synchronized void putToRedis(String userKey, String userInfo) {
        HashMap userHashMap = (HashMap) redisService.get(redisLoginKey);
        if (userHashMap == null) {
            userHashMap = new HashMap();
        }
        userHashMap.put(userKey, userInfo);
        redisService.set(redisLoginKey, userHashMap, redisTimes);
    }

    public void removeRedisValue(String userKey) {
        if (StringUtils.isBlank(userKey) || redisService == null) {
            return;
        }
        removeRedis(userKey);
    }

    private synchronized void removeRedis(String userKey) {
        HashMap userHashMap = (HashMap) redisService.get(redisLoginKey);
        if (userHashMap == null) {
            userHashMap = new HashMap();
        }
        userHashMap.remove(userKey);
        redisService.set(redisLoginKey, userHashMap, redisTimes);
    }

    public UserInfoModel getRedisValue(String userKey) {
        HashMap userHashMap = (HashMap) redisService.get(redisLoginKey);
        if (userHashMap == null) {
            return null;
        }

        String userObj = (String) userHashMap.get(userKey);
        if (StringUtils.isBlank(userObj)) {
            return null;
        }

        ObjectMapper objectMapper = JacksonUtils.getInstance();
        try {
            return objectMapper.readValue(userObj, UserInfoModel.class);
        } catch (IOException e) {
            logger.error("SecurityService getRedisValue(String userKey)", e);
            return null;
        }
    }

    public void addCookieValue(HttpServletResponse response, String userKey) {
        addCookieValue(response, userKey, null);
    }

    public void addCookieValue(HttpServletResponse response, String userKey, String domain) {
        try {
            String aesUserKey;
            if (StringUtils.isNotBlank(aes128CookieKey)) {
                aesUserKey = AES128Utils.encrypt(userKey, aes128CookieKey);
            } else {
                aesUserKey = userKey;
            }
            CookieUtils.addCookie(response, loginCookieKey, aesUserKey, domain, cookieTimes);
        } catch (Exception e) {
            logger.error("SecurityService addCookieValue error", e);
        }
    }

    public String getCookieValue(HttpServletRequest request) {
        String loginCookieData = CookieUtils.getValueByName(request, loginCookieKey);
        if (StringUtils.isBlank(loginCookieData)) {
            return null;
        }

        if (StringUtils.isNotBlank(aes128CookieKey)) {
            try {
                return AES128Utils.decrypt(loginCookieData, aes128CookieKey);
            } catch (Exception e) {
                logger.error("SecurityService getCookieValue error", e);
                return null;
            }
        } else {
            return loginCookieData;
        }
    }

    public Boolean isLogin(HttpServletRequest request) {
        String userKey = getCookieValue(request);
        if (StringUtils.isBlank(userKey)) {
            return false;
        }

        UserInfoModel redisValue = getRedisValue(userKey);
        if (redisValue == null) {
            return false;
        }

        return true;
    }
}

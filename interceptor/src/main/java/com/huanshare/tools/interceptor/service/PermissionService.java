package com.huanshare.tools.interceptor.service;

import com.huanshare.tools.interceptor.enums.PermissionTypeEnum;
import com.huanshare.tools.interceptor.model.PermissionInfosModel;
import com.huanshare.tools.interceptor.model.PermissionModel;
import com.huanshare.tools.redis.RedisService;
import com.huanshare.tools.utils.JacksonUtils;
import com.huanshare.tools.utils.ListUtils;
import com.huanshare.tools.utils.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.*;

/**
 *  2018/11/14.
 */
public class PermissionService {
    private static Logger logger = Logger.getLogger(PermissionService.class);

    private long redisTimes = 1 * 24 * 60 * 60;

    private RedisService redisService;

    private String applicationCode;
    private String redisPermissionKey;

    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    public void setRedisPermissionKey(String redisPermissionKey) {
        this.redisPermissionKey = redisPermissionKey;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public void setRedisTimes(long redisTimes) {
        this.redisTimes = redisTimes;
    }

    public void setValue(String userKey, List<PermissionInfosModel> permissionInfosModels){
        //Map<用户,Map<应用,Map<权限类型,Map<权限Code,权限对象>>>>
        if(StringUtils.isBlank(userKey) || StringUtils.isBlank(redisPermissionKey) || redisService == null){
            return;
        }

        Map<String, Map<String, Map<String, String>>> userAppHashMap = new LinkedHashMap<>();
        if(ListUtils.isNotEmpty(permissionInfosModels)){
            ObjectMapper objectMapper = JacksonUtils.getInstance();
            //循环应用
            for(PermissionInfosModel permissionInfosModel : permissionInfosModels){
                List<PermissionModel> permissionList = permissionInfosModel.getPermissionList();

                Map<String, String> userMenuPermissionHashMap = new LinkedHashMap();
                Map<String, String> userFunctionPermissionHashMap = new LinkedHashMap();
                if(ListUtils.isNotEmpty(permissionList)) {
                    //菜单
                    putPermissionToMapByType(userMenuPermissionHashMap, permissionList, PermissionTypeEnum.MENU, objectMapper);
                    //功能
                    putPermissionToMapByType(userFunctionPermissionHashMap, permissionList, PermissionTypeEnum.FUNCTION, objectMapper);
                }
                Map<String, Map<String, String>> permissionTypeHashMap = new LinkedHashMap();
                permissionTypeHashMap.put(PermissionTypeEnum.MENU.getCode(), userMenuPermissionHashMap);
                permissionTypeHashMap.put(PermissionTypeEnum.FUNCTION.getCode(), userFunctionPermissionHashMap);

                userAppHashMap.put(permissionInfosModel.getApplicationCode(), permissionTypeHashMap);
            }
        }
        putToRedis(userKey, userAppHashMap);
    }

    private void putPermissionToMapByType(Map<String, String> userPermissionHashMap, List<PermissionModel> permissionList, PermissionTypeEnum permissionTypeEnum, ObjectMapper objectMapper){
        String permissionType = String.valueOf(permissionTypeEnum.getKey());
        for (PermissionModel permissionModel : permissionList) {
            if(permissionType.equals(permissionModel.getPermissionType())){
                try {
                    //应用下面的权限MAP
                    userPermissionHashMap.put(permissionModel.getPermissionCode(), objectMapper.writeValueAsString(permissionModel));
                } catch (IOException e) {
                    logger.error("PermissionService putPermissionToMapByType userPermissionHashMap error", e);
                }
            }
        }
    }

    private synchronized void putToRedis(String userKey, Map userAppHashMap){
        HashMap userHashMap = (HashMap)redisService.get(redisPermissionKey);
        if(userHashMap == null){
            userHashMap = new HashMap();
        }
        userHashMap.put(userKey, userAppHashMap);
        redisService.set(redisPermissionKey, userHashMap, redisTimes);
    }

    public void removeValue(String userKey){
        if(StringUtils.isBlank(userKey) || StringUtils.isBlank(redisPermissionKey) || redisService == null){
            return;
        }
        removeRedis(userKey);
    }

    private synchronized void removeRedis(String userKey){
        HashMap userHashMap = (HashMap)redisService.get(redisPermissionKey);
        if(userHashMap == null){
            userHashMap = new HashMap();
        }
        userHashMap.remove(userKey);
        redisService.set(redisPermissionKey, userHashMap, redisTimes);
    }

    private Map getValueMap(String userKey, PermissionTypeEnum permissionTypeEnum){
        if(StringUtils.isBlank(userKey) || permissionTypeEnum == null){
            return null;
        }
        HashMap userHashMap = (HashMap)redisService.get(redisPermissionKey);
        if(userHashMap == null){
            return null;
        }
        //应用
        HashMap userAppHashMap = (HashMap)userHashMap.get(userKey);
        if(userAppHashMap == null){
            return null;
        }
        //类型
        HashMap permissionTypeHashMap = (HashMap)userAppHashMap.get(applicationCode);
        if(permissionTypeHashMap == null){
            return null;
        }
        //权限
        HashMap userPermissionHashMap = (HashMap)permissionTypeHashMap.get(permissionTypeEnum.getCode());
        if(userPermissionHashMap == null){
            return null;
        }

        return userPermissionHashMap;
    }

    public PermissionModel getValue(String userKey, PermissionTypeEnum permissionTypeEnum, String permissionCode){
        Map valueMap = getValueMap(userKey, permissionTypeEnum);
        if(valueMap == null){
            return  null;
        }
        String permissionObj = (String)valueMap.get(permissionCode);
        if(StringUtils.isBlank(permissionObj)){
            return null;
        }
        ObjectMapper objectMapper = JacksonUtils.getInstance();
        try {
            return objectMapper.readValue(permissionObj, PermissionModel.class);
        } catch (IOException e) {
            logger.error("PermissionService getValue(String userKey, PermissionTypeEnum permissionTypeEnum, String permissionCode)", e);
            return null;
        }
    }

    public List<PermissionModel> getValue(String userKey, PermissionTypeEnum permissionTypeEnum){
        Map valueMap = getValueMap(userKey, permissionTypeEnum);
        if(valueMap == null){
            return  null;
        }

        List<PermissionModel> returnList = new ArrayList<>();
        ObjectMapper objectMapper = JacksonUtils.getInstance();
        Collection<String> values = valueMap.values();
        String permissionType = String.valueOf(permissionTypeEnum.getKey());
        for(String permissionObj : values){
            try {
                PermissionModel permissionModel = objectMapper.readValue(permissionObj, PermissionModel.class);
                returnList.add(permissionModel);
            } catch (IOException e) {
                logger.error("PermissionService getValue(String userKey, PermissionTypeEnum permissionTypeEnum)", e);
            }
        }
        return returnList;
    }
}

package com.huanshare.tools.mvc.utils;

import com.huanshare.tools.mvc.manager.ParamsBean;
import com.huanshare.tools.mapper.utils.ObjectUtil;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import com.huanshare.tools.utils.CommonUtils;
import com.huanshare.tools.utils.HttpUtils;
import com.huanshare.tools.utils.JacksonUtils;
import com.huanshare.tools.utils.SecurityUtil;
import com.huanshare.tools.mapper.utils.ObjectUtil;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsBean;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import com.huanshare.tools.utils.HttpUtils;
import com.huanshare.tools.utils.JacksonUtils;
import com.huanshare.tools.utils.SecurityUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  2018/4/12.
 */
public class RestClient {
    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RestClient.class);
    private static Map<String, String> cacheMap = new ConcurrentHashMap<>();

    private RestTemplate restTemplate;

    //JSON处理类
    private ObjectMapper objectMapper = JacksonUtils.getInstance();
    //参数ThreadLocal处理类
    private ParamsThreadLocalManager paramsThreadLocalManager = ParamsThreadLocalManager.getInstance();

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        //添加完毕模板后进行加载配置文件
        reloadRuntimeProperties();
    }

    public <T> T requestL2(String restUrl, String restApi, String appId, String secretKey, Object requestModel, Class<T> resultClass) throws Exception {
        Map<String, Object> requestMap = ObjectUtil.objectToMap(requestModel);
        requestMap.put(RestBaseConstant.PARAMS_APPID, appId);
        requestMap.put(RestBaseConstant.PARAMS_EVENT_TIME, String.valueOf(new Date().getTime()));
        //md5加密
        String md5 = SecurityUtil.getMd5Sign(requestMap, secretKey);
        requestMap.put(RestBaseConstant.PARAMS_SIGN, md5);

        return request(restUrl, restApi, requestMap, null, resultClass);
    }

    public <T> T request(String restUrl, String restApi, Object requestModel, Class<T> resultClass) {
        return request(restUrl, restApi, requestModel, null, resultClass);
    }

    public <T> T request(String restUrl, String restApi, Object requestModel, Map<String, String> headerMap, Class<T> resultClass) {
        if (StringUtils.isBlank(restUrl) || StringUtils.isBlank(restApi)) {
            return null;
        }

        String fromSourceId;
        ParamsBean params = paramsThreadLocalManager.getParams();
        Map<String, Object> paramValueMap = params != null ? params.getAllRequestParamsMap() : null;
        if (paramValueMap != null) {
            fromSourceId = CommonUtils.getString(paramValueMap.get(RestBaseConstant.FROMSOURCEID));
        } else {
            fromSourceId = UUID.randomUUID().toString();
        }
        if (headerMap == null) {
            headerMap = new HashMap<>();
        }
        //读取配置中的别名作为调用方
        String fromSource = cacheMap.get("runtime.systemAlias");
        if (StringUtils.isNotBlank(fromSource)) {
            headerMap.put("from_source", fromSource);
            headerMap.put("from_source_id", fromSourceId);
        }

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        if (MapUtils.isNotEmpty(headerMap)) {
            Set<Map.Entry<String, String>> entries = headerMap.entrySet();
            for (Iterator<Map.Entry<String, String>> iterator = entries.iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> entry = iterator.next();
                headers.add(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity<Object> formEntity = new HttpEntity<>(requestModel, headers);

        String postUrl = restUrl + restApi;
        //Transaction transaction = Cat.newTransaction("Request", postUrl);
        T resultContent = null;
        long startTime = System.currentTimeMillis();

        String requestHeader = "\n request header : " + getObjectContent(headerMap);
        String requestContent = "\n , request content : " + getObjectContent(requestModel);
        try {
            resultContent = restTemplate.postForObject(postUrl, formEntity, resultClass);
            //transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception ex) {
            //Cat.getProducer().logError(ex);
            //transaction.setStatus(ex);
            logger.error("restClient request url : " + postUrl + requestHeader + requestContent + "\n , error message : " + ex.getMessage(), ex);
            resultContent = null;
        } finally {
            //transaction.complete();
            long endTime = System.currentTimeMillis();
            //输出LOG
            String responseContent = "\n , response result : " + getObjectContent(resultContent);
            logger.info("restClient request url : " + postUrl + " , use time : " + (endTime - startTime) + requestHeader + requestContent + responseContent);
        }

        return resultContent;
    }

    private void reloadRuntimeProperties() {
        InputStreamReader configStream = null;
        try {
            configStream = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("runtime.properties"), HttpUtils.UTF8);
            Properties configProperties = new Properties();
            configProperties.load(configStream);
            Set<Object> configObjSet = configProperties.keySet();
            for (Iterator<Object> iterator = configObjSet.iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                cacheMap.put(key, configProperties.getProperty(key));
            }
        } catch (IOException e) {
            logger.info("reloadRuntimeProperties error.", e);
        } finally {
            if (configStream != null) {
                try {
                    configStream.close();
                } catch (IOException e) {
                    throw new RuntimeException("close runtimeProperties error.", e);
                }
            }
        }
    }

    private String getObjectContent(Object object) {
        if (object == null) {
            return "";
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error("getObjectContent writeValueAsString error." + e.getMessage(), e);
        }
        return "";
    }
}

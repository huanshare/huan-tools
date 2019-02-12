package com.huanshare.tools.mvc.aspect;

import com.huanshare.tools.mvc.annotation.rest.RestLevel;
import com.huanshare.tools.mvc.cache.RestAspectPropertiesManager;
import com.huanshare.tools.mvc.manager.ParamsBean;
import com.huanshare.tools.mvc.manager.ParamsManager;
import com.huanshare.tools.mvc.model.RestAspectProperties;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import com.huanshare.tools.utils.SecurityUtil;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.mvc.annotation.rest.RestLevel;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import com.huanshare.tools.utils.SecurityUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *  2017/5/18.
 */
public class RestLevelAspect {
    private Logger logger = LoggerFactory.getLogger(RestLevelAspect.class);
    //参数处理类
    private ParamsManager paramsManager = ParamsManager.getInstance();
    //参数ThreadLocal处理类
    private ParamsThreadLocalManager paramsThreadLocalManager = ParamsThreadLocalManager.getInstance();
    //配置类
    private RestAspectPropertiesManager restAspectPropertiesManager = RestAspectPropertiesManager.getInstance();

    public RestAspectProperties getProperties() {
        return restAspectPropertiesManager.getProperties();
    }

    public void setProperties(RestAspectProperties properties) {
        this.restAspectPropertiesManager.setProperties(properties);
    }

    public static Boolean validateL2(Map<String, Object> requestMap, String appIdV, String secretKey) {
        String appId = (String) requestMap.get(RestBaseConstant.PARAMS_APPID);
        String sign = (String) requestMap.get(RestBaseConstant.PARAMS_SIGN);
        String eventTime = (String) requestMap.get(RestBaseConstant.PARAMS_EVENT_TIME);

        // 1.验证系统参数是否存在。
        if (StringUtils.isBlank(appId)) {
            throw new BizErrorBusinessException(RestBaseConstant.EMPTY_APPID);
        }

        if (StringUtils.isBlank(sign)) {
            throw new BizErrorBusinessException(RestBaseConstant.EMPTY_SIGN);
        }

        if (StringUtils.isBlank(eventTime)) {
            throw new BizErrorBusinessException(RestBaseConstant.EMPTY_EVENT_TIME);
        }
        // 2.验证API是否正确
        if (!appId.equals(appIdV)) {
            throw new BizErrorBusinessException(RestBaseConstant.ERROR_SECRET);
        }

        // 3.验证签名是否正确
        String md5 = SecurityUtil.getMd5Sign(requestMap, secretKey);
        if (!sign.equals(md5)) {
            throw new BizErrorBusinessException(RestBaseConstant.ERROR_SIGN);
        }

        return true;
    }

    public Object processAspect(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        //获取方法的参数名称和值
        ParamsBean params = paramsThreadLocalManager.getParams();
        Map<String, Object> paramValueMap = params != null ? params.getRequestParamsMap() : null;
        if (paramValueMap == null) {
            Class<?> aClass = point.getTarget().getClass();
            paramValueMap = paramsManager.getParamValueMap(aClass, method, args, ParamsManager.EXCLUDE_PARAMS);
            paramsThreadLocalManager.putParams(paramValueMap);
        }

        //协议检查
        validLimitedMethod(method.getAnnotation(RestLevel.class), paramValueMap);

        return point.proceed();
    }


    private void validLimitedMethod(RestLevel restLevel, Map<String, Object> requestParams) throws IOException, BizErrorBusinessException {
        if (restLevel == null) {
            return;
        }

        Boolean isValidation = false;
        //L1验证
        if (RestLevel.LEVEL_ONE == restLevel.level()) {
            isValidation = true;
        }
        //L2验证
        else if (RestLevel.LEVEL_TWO == restLevel.level()
                && validateL2(requestParams, getProperties().getAppId(), getProperties().getSecretKey())) {
            isValidation = true;
        }

        if (!isValidation) {
            throw new BizErrorBusinessException(RestBaseConstant.OPERATOR_HTTP_VALID_FAILURE);
        }
    }
}

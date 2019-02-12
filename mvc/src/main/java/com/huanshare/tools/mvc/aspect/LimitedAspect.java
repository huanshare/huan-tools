package com.huanshare.tools.mvc.aspect;

import com.huanshare.tools.mvc.manager.ParamsManager;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.mvc.annotation.limited.ExtFrequency;
import com.huanshare.tools.mvc.annotation.limited.LimitedExtFrequency;
import com.huanshare.tools.mvc.annotation.limited.LimitedFrequency;
import com.huanshare.tools.mvc.annotation.limited.LimitedIdempotent;
import com.huanshare.tools.mvc.constant.ErrorCodeConstant;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsBean;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import com.huanshare.tools.redis.RedisService;
import com.huanshare.tools.utils.JacksonUtils;
import com.huanshare.tools.utils.MD5Utils;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.mvc.annotation.limited.ExtFrequency;
import com.huanshare.tools.mvc.annotation.limited.LimitedExtFrequency;
import com.huanshare.tools.mvc.annotation.limited.LimitedFrequency;
import com.huanshare.tools.mvc.annotation.limited.LimitedIdempotent;
import com.huanshare.tools.mvc.constant.ErrorCodeConstant;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import com.huanshare.tools.redis.RedisService;
import com.huanshare.tools.utils.JacksonUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 限制请求拦截
 *  2018/3/31.
 */
public class LimitedAspect {

    private static String VALID_IDEMPOTENT_KEY_TITLE = "LGS_TOOLS_LIMITED_IDEMPOTENT_";
    private static String VALID_LIMITED_EXT_METHOD_KEY_TITLE = "LGS_TOOLS_LIMITED_EXT_METHOD_";
    private static String VALID_LIMITED_METHOD_KEY_TITLE = "LGS_TOOLS_LIMITED_METHOD_";
    private Logger logger = LoggerFactory.getLogger(LimitedAspect.class);

    //参数处理类
    private ParamsManager paramsManager = ParamsManager.getInstance();
    //参数ThreadLocal处理类
    private ParamsThreadLocalManager paramsThreadLocalManager = ParamsThreadLocalManager.getInstance();
    private RedisService redisServiceUtil;

    public RedisService getRedisServiceUtil() {
        return redisServiceUtil;
    }

    public void setRedisServiceUtil(RedisService redisServiceUtil) {
        this.redisServiceUtil = redisServiceUtil;
    }

    public Object processAspect(ProceedingJoinPoint point) throws Throwable {
        //业务幂等拦截
        Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        String methodName = method.getName();
        //获取方法的参数名称和值
        ParamsBean params = paramsThreadLocalManager.getParams();
        if (params == null) {
            Class<?> aClass = point.getTarget().getClass();
            Map<String, Object> paramValueMap = paramsManager.getParamValueMap(aClass, method, args, ParamsManager.EXCLUDE_PARAMS);
            paramsThreadLocalManager.putParams(paramValueMap);
            params = paramsThreadLocalManager.getParams();
        }

        //全局方法名限流
        validLimitedMethod(method.getAnnotation(LimitedFrequency.class), method.getName());

        //扩展限流
        validLimitedExtMethod(method.getAnnotation(LimitedExtFrequency.class), method.getName(), params.getAllRequestParamsMap());

        //业务幂等
        validIdempotent(method.getAnnotation(LimitedIdempotent.class), methodName, params.getRequestParamsMap());

        return point.proceed();
    }

    //业务幂等拦截
    private void validIdempotent(LimitedIdempotent limitedIdempotent, String methodName, Map<String, Object> paramValueMap) throws IOException, BizErrorBusinessException {
        if (limitedIdempotent == null || redisServiceUtil == null) {
            return;
        }

        Map<String, Object> keyNameMap = getKeyNames(limitedIdempotent.params(), limitedIdempotent.excludeParams(), paramValueMap);
        String idempotentKey = generateLimitedKey(VALID_IDEMPOTENT_KEY_TITLE, methodName, keyNameMap);
        if (StringUtils.isBlank(idempotentKey)) {
            return;
        }

        Long increment = redisServiceUtil.increment(idempotentKey, 1L);
        if (increment > 1L) {
            String message = limitedIdempotent.message();
            throw new BizErrorBusinessException(StringUtils.isBlank(message) ? RestBaseConstant.REQUEST_LIMIT_IDEMPOTENT_REPEAT : message, ErrorCodeConstant.ERROR_CODE_10006);
        } else if (increment == 1L) {
            //设置过期
            redisServiceUtil.expire(idempotentKey, limitedIdempotent.timeout());
        }
    }

    //扩展限制拦截
    private void validLimitedExtMethod(LimitedExtFrequency limitedExtFrequency, String methodName, Map<String, Object> paramValueMap) throws IOException, BizErrorBusinessException {
        //对方法进行限流
        if (limitedExtFrequency == null || redisServiceUtil == null) {
            return;
        }

        ExtFrequency[] limiteds = limitedExtFrequency.limiteds();
        if (limiteds != null && limiteds.length > 0) {
            for (ExtFrequency extFrequency : limiteds) {
                Map<String, Object> keyNameMap = getKeyNames(extFrequency.params(), extFrequency.excludeParams(), paramValueMap);
                String limitedMethodKey = generateLimitedKey(VALID_LIMITED_EXT_METHOD_KEY_TITLE, methodName, keyNameMap);
                if (StringUtils.isBlank(limitedMethodKey)) {
                    return;
                }

                Long increment = redisServiceUtil.increment(limitedMethodKey, 1L);
                if (increment > extFrequency.count()) {
                    String message = extFrequency.message();
                    throw new BizErrorBusinessException(StringUtils.isBlank(message) ? RestBaseConstant.REQUEST_LIMIT_EXT_METHOD_REPEAT : message, ErrorCodeConstant.ERROR_CODE_10008);
                } else if (increment == 1L) {
                    //设置过期
                    redisServiceUtil.expire(limitedMethodKey, extFrequency.timeout());
                }
            }
        }
    }

    //全局方法名限制拦截
    private void validLimitedMethod(LimitedFrequency limitedFrequency, String methodName) throws IOException, BizErrorBusinessException {
        //对方法进行限流
        if (limitedFrequency == null || redisServiceUtil == null) {
            return;
        }

        StringBuilder strSB = new StringBuilder(VALID_LIMITED_METHOD_KEY_TITLE);
        strSB.append(methodName.toUpperCase());
        String limitedMethodKey = strSB.toString();
        if (StringUtils.isBlank(limitedMethodKey)) {
            return;
        }

        Long increment = redisServiceUtil.increment(limitedMethodKey, 1L);
        if (increment > limitedFrequency.count()) {
            String message = limitedFrequency.message();
            throw new BizErrorBusinessException(StringUtils.isBlank(message) ? RestBaseConstant.REQUEST_LIMIT_METHOD_REPEAT : message, ErrorCodeConstant.ERROR_CODE_10007);
        } else if (increment == 1L) {
            //设置过期
            redisServiceUtil.expire(limitedMethodKey, limitedFrequency.timeout());
        }
    }

    private String generateLimitedKey(String redisKeyTitle, String methodName, Map<String, Object> keyNameMap) throws IOException {
        if (keyNameMap == null) {
            return null;
        }

        StringBuilder strSB = new StringBuilder(redisKeyTitle);
        strSB.append(methodName.toUpperCase()).append("_");

        StringBuilder strSB2 = new StringBuilder();
        Set<Map.Entry<String, Object>> entries = keyNameMap.entrySet();
        for (Iterator<Map.Entry<String, Object>> iterator = entries.iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            strSB2.append(entry.getKey()).append(entry.getValue());
        }
        String md5 = MD5Utils.encode(strSB2.toString());
        strSB.append(md5.toUpperCase());

        return strSB.toString();
    }

    //获取需要过滤的KEY和VALUE
    private Map<String, Object> getKeyNames(String keyNames, String excludeKeyNames, Map<String, Object> paramValueMap) {
        Map<String, Object> keyNameMap = new HashMap<>();

        if (StringUtils.isBlank(keyNames)) {
            keyNameMap.putAll(paramValueMap);
        } else {
            String[] keyNameList = StringUtils.split(keyNames, ",");
            for (String keyName : keyNameList) {
                if (paramValueMap.containsKey(keyName)) {
                    keyNameMap.put(keyName, getMapValue(keyName, paramValueMap));
                }
            }
        }

        //排除不需要的
        if (StringUtils.isNotBlank(excludeKeyNames)) {
            String[] excludeKeyNameList = StringUtils.split(excludeKeyNames, ",");
            for (String keyName : excludeKeyNameList) {
                if (keyNameMap.containsKey(keyName)) {
                    keyNameMap.remove(keyName);
                }
            }
        }

        return keyNameMap;
    }

    public Object getMapValue(String key, Map<String, Object> paramValueMap) {
        String[] strings = StringUtils.split(key, ".");
        if (strings.length > 0) {
            Map<String, Object> tempMap = paramValueMap;
            Object tempObj;
            for (int i = 0; i < strings.length; i++) {
                tempObj = tempMap.get(strings[i]);
                if (i == strings.length - 1) {
                    return tempObj;
                } else {
                    if (!(tempObj instanceof Map)) {
                        break;
                    }
                    tempMap = (Map<String, Object>) tempObj;
                }
            }
            return null;
        } else {
            return paramValueMap.get(key);
        }
    }

    public static void main(String[] args) {
        String str = "{\"ProtocolVersion\":1,\"TimeStamp\":1499750253622,\"orderId\":20822,\"orderCode\":\"WLD20170707110021\",\"operateId\":1115,\"operateBy\":\"占辉\",\"operateTime\":1499750253000,\"statusType\":2220,\"billDtoList\":[],\"pickUpDto\":{\"pickOutType\":1,\"measuringType\":0,\"pickOutWeight\":59.719,\"incidentalExpenses\":0.00,\"orderUnitPrice\":50.00,\"orderFixedPrice\":2985.95,\"carrierExpenses\":2985.95,\"orderType\":1}}";
        Map map = null;
        try {
            map = JacksonUtils.getInstance().readValue(str, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LimitedAspect limitedAspect = new LimitedAspect();
        System.out.println(limitedAspect.getMapValue("pickUpDto.carrierExpenses", map));
        System.out.println(limitedAspect.getMapValue("pickUpDto.pickOutWeight", map));
        System.out.println(limitedAspect.getMapValue("orderId", map));
        System.out.println(limitedAspect.getMapValue("operateBy", map));
    }
}

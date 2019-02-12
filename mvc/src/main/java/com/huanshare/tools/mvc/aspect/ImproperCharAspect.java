package com.huanshare.tools.mvc.aspect;

import com.huanshare.tools.mvc.manager.ParamsManager;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.mvc.annotation.rest.ImproperChar;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsBean;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.mvc.annotation.rest.ImproperChar;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *  2017/5/18.
 */
public class ImproperCharAspect {
    private Logger logger = LoggerFactory.getLogger(ImproperCharAspect.class);
    //参数处理类
    private ParamsManager paramsManager = ParamsManager.getInstance();
    //参数ThreadLocal处理类
    private ParamsThreadLocalManager paramsThreadLocalManager = ParamsThreadLocalManager.getInstance();

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

        //验证非法字符
        validImproperChar(method.getAnnotation(ImproperChar.class), paramValueMap);

        return point.proceed();
    }


    private void validImproperChar(ImproperChar improperChar, Map<String, Object> requestParams) throws IOException, BizErrorBusinessException {
        if (improperChar == null) {
            return;
        }

        String regex = improperChar.regex();
        if(StringUtils.isEmpty(regex)){
            return;
        }

        Pattern p = Pattern.compile(regex);
        Set<Map.Entry<String, Object>> entries = requestParams.entrySet();
        for(Iterator<Map.Entry<String, Object>> iterator = entries.iterator();iterator.hasNext();){
            Map.Entry<String, Object> entry = iterator.next();
            Object value = entry.getValue();
            if(value instanceof String && p.matcher(value.toString()).find()){
                throw new BizErrorBusinessException(entry.getKey() + RestBaseConstant.IMPROPER_CHAR_ERROR);
            }
        }
    }
}

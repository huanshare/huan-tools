package com.huanshare.tools.mvc.aspect.params;

import com.huanshare.tools.mvc.manager.ParamsBean;
import com.huanshare.tools.mvc.manager.ParamsManager;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.exception.BizTokenException;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import com.huanshare.tools.mvc.model.ExtDefineParamsInfo;
import com.huanshare.tools.utils.Base64Utils;
import com.huanshare.tools.utils.JacksonUtils;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.mvc.constant.RestBaseConstant;
import com.huanshare.tools.mvc.manager.ParamsThreadLocalManager;
import com.huanshare.tools.utils.JacksonUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 参数拦截
 *  2018/3/31.
 */
public class ParamsAspect {

    private Logger logger = LoggerFactory.getLogger(ParamsAspect.class);
    //参数处理类
    private ParamsManager paramsManager = ParamsManager.getInstance();
    //参数ThreadLocal处理类
    private ParamsThreadLocalManager paramsThreadLocalManager = ParamsThreadLocalManager.getInstance();
    //JSON处理类
    private ObjectMapper objectMapper = JacksonUtils.getInstance();
    //返回处理类
    private AbstractParamsReturnHandler paramsReturnHandler;

    public void setParamsReturnHandler(AbstractParamsReturnHandler paramsReturnHandler) {
        this.paramsReturnHandler = paramsReturnHandler;
    }

    public Object processAspect(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> aClass = point.getTarget().getClass();

        String urlPath = getUrlPath(method);
        Map<String, Object> requestMap = null;
        String responseContent = "";
        Object dto;
        try {
            Map<String, Object> requestParamsMap = paramsManager.getParamValueMap(aClass, method, args, ParamsManager.EXCLUDE_PARAMS);
            Map<String, Object> extDefineParams = getExtDefineParams();
            paramsThreadLocalManager.putParams(requestParamsMap, extDefineParams);
            ParamsBean params = paramsThreadLocalManager.getParams();
            requestMap = params.getAllRequestParamsMap();

            dto = point.proceed(getProceedArgs(args, requestMap));
            if (paramsReturnHandler != null) {
                paramsReturnHandler.doReturnHandler(dto, requestMap);
            }

            responseContent = "\n , response result : " + getObjectContent(dto);
        } catch (Exception ex) {
            responseContent = "\n , response error msg : " + ex.getMessage();

            //不打error类型的业务LOG
            if (!(ex instanceof BizErrorBusinessException) && !(ex instanceof BizTokenException)) {
                logger.error(urlPath + "\n , request content : " + getObjectContent(requestMap) + "\n , error msg : " + ex.getMessage(), ex);
            }
            throw ex;
        } finally {
            String requestContent = "\n , request content : " + getObjectContent(requestMap);
            logger.info(urlPath + requestContent + responseContent);
        }

        return dto;
    }

    protected Map<String, Object> getExtDefineParams() throws ClassNotFoundException {
        Map<String, Object> extDefineMap = new HashMap<>();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 默认添加IP
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        extDefineMap.put(RestBaseConstant.REQUEST_IP, ip);
        //设备信息
        String applicationInfo = request.getHeader(RestBaseConstant.APPLICATION_INFO);
        if (StringUtils.isNotBlank(applicationInfo)) {
            extDefineMap.put(RestBaseConstant.APPLICATION_INFO_OLD, applicationInfo);
            try {
                extDefineMap.put(RestBaseConstant.APPLICATION_INFO, Base64Utils.decode(applicationInfo));
            } catch (Exception e) {
                logger.error(" base64 error info:" + applicationInfo, e);
            }
        }
        String userAgent = request.getHeader(RestBaseConstant.USER_AGENT);
        if (StringUtils.isNotBlank(userAgent)) {
            extDefineMap.put(RestBaseConstant.USER_AGENT, userAgent);
        }
        String fromSource = request.getHeader(RestBaseConstant.FROM_SOURCE);
        if (StringUtils.isNotBlank(fromSource)) {
            extDefineMap.put(RestBaseConstant.FROMSOURCE, fromSource);
        }
        //来源的UUID，为空时自动生成一个
        String fromSourceId = request.getHeader(RestBaseConstant.FROM_SOURCE_ID);
        if (StringUtils.isBlank(fromSourceId)) {
            fromSourceId = UUID.randomUUID().toString();
        }
        extDefineMap.put(RestBaseConstant.FROMSOURCEID, fromSourceId);
        return extDefineMap;
    }

    protected Object[] getProceedArgs(Object[] args, Map<String, Object> requestMap) throws Exception {
        if(args == null || args.length <= 0){
            return args;
        }

        Object[] newArgs = new Object[args.length];
        for(int i=0; i<args.length; i++){
            Object obj = args[i];
            if(obj instanceof ExtDefineParamsInfo){
                newArgs[i] = objectMapper.readValue(objectMapper.writeValueAsString(requestMap), args[i].getClass());
            }else{
                newArgs[i] = obj;
            }
        }

        return newArgs;
    }

    private String getUrlPath(Method method) {
        RequestMapping map = method.getAnnotation(RequestMapping.class);
        String[] strs = map.value();
        if (strs != null && strs.length > 0) {
            return strs[0];
        }

        return "";
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

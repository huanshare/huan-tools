package com.huanshare.tools.mvc.manager;

import com.huanshare.tools.mvc.model.ExtDefineParamsInfo;
import com.huanshare.tools.utils.JacksonUtils;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.utils.JacksonUtils;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  2017/5/5.
 */
public class ParamsManager {

    private static Logger logger = Logger.getLogger(ParamsManager.class);
    //参数过滤掉的类型
    public static Class[] EXCLUDE_PARAMS = new Class[]{HttpServletRequest.class, HttpServletResponse.class, ExtDefineParamsInfo.class};

    //JSON处理类
    private ObjectMapper objectMapper = JacksonUtils.getInstance();
    private static ParamsManager manager = null;
    private ClassPool classPool;

    {
        classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(getClass()));
    }

    private ParamsManager() {
    }

    public static synchronized ParamsManager getInstance() {
        if (manager == null) {
            manager = new ParamsManager();
        }
        return manager;
    }

    public Map<String, Object> getParamValueMap(Class clazz, Method method, Object[] args) {
        return getParamValueMap(clazz, method, args, null);
    }

    //根据类、方法、参数值返回值Map
    public Map<String, Object> getParamValueMap(Class clazz, Method method, Object[] args, Class[] excludeType) {
        Set<String> excludeTypeSet = new HashSet<>();
        if (excludeType != null && excludeType.length > 0) {
            for (Class classType : excludeType) {
                excludeTypeSet.add(classType.getName());
            }
        }

        Map<String, Object> paramValueMap = new HashMap<>();
        try {
            CtClass ctClass = classPool.get(clazz.getName());
            CtClass[] parameterTypes = new CtClass[args.length];
            Class<?>[] parameterTypeList = method.getParameterTypes();
            for (int i = 0; i < parameterTypeList.length; i++) {
                parameterTypes[i] = classPool.get(parameterTypeList[i].getName());
            }
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName(), parameterTypes);
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                logger.warn("can not found localVariableAttribute, methodName:" + method.getName());
                return null;
            }

            int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
            for (int i = 0; i < args.length; i++) {
                Class<?> paramsTypeClass = parameterTypeList[i];
                if (excludeTypeSet.contains(paramsTypeClass.getName())) {
                    continue;
                }

                //为空或者是基础类型就直接添加
                if (args[i] == null || isWrapClass(paramsTypeClass)) {
                    String variableName = attr.variableName(i + pos);
                    if (StringUtils.isNotBlank(variableName)) {
                        paramValueMap.put(variableName, args[i]);
                    }
                } else {
                    try {
                        String json = objectMapper.writeValueAsString(args[i]);
                        paramValueMap.putAll(objectMapper.readValue(json, Map.class));
                    } catch (IOException e) {
                        logger.error("ParamsManager getParamValueMap json error." + e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("ParamsManager getParamValueMap error, methodName:" + method.getName(), e);
        }
        return paramValueMap;
    }

    public Boolean isWrapClass(Class clz) {
        Boolean isWrapClass;
        try {
            isWrapClass = ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            isWrapClass = false;
        }

        return isWrapClass || String.class.getName().equals(clz.getName());
    }

}

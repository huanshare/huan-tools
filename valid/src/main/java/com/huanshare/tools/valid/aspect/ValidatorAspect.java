package com.huanshare.tools.valid.aspect;

import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.valid.manager.ValidManager;
import com.huanshare.tools.valid.validtor.ParamsValid;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.valid.validtor.ParamsValid;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 验证拦截
 *
 */
public class ValidatorAspect {

    private static Logger logger = LoggerFactory.getLogger(ValidatorAspect.class);
    private static ValidManager validManager = ValidManager.getInstance();

    public Object processAspect(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        checkRestValid(method, args);

        return point.proceed();
    }

    private void checkRestValid(Method method, Object[] args) throws BizErrorBusinessException {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations != null && parameterAnnotations.length > 0) {
            //多个参数循环
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] paramAnn = parameterAnnotations[i];
                if (paramAnn != null && paramAnn.length > 0) {
                    //参数有多个注解
                    for (int j = 0; j < paramAnn.length; j++) {
                        Annotation annotation = paramAnn[j];
                        if (ParamsValid.class.isInstance(annotation)) {
                            validManager.valid(args[i]);
                            break;
                        }
                    }
                }
            }
        }
    }
}

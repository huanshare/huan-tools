package com.huanshare.tools.valid.manager;

import com.huanshare.tools.valid.validator.ConstraintValidator;
import com.huanshare.tools.valid.validtor.ValidConstraint;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.valid.validator.ConstraintValidator;
import com.huanshare.tools.valid.validtor.ValidConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ValidManager {

	private static Logger logger = LoggerFactory.getLogger(ValidManager.class);
	private static ValidManager validManager = null;

	private ValidManager() {
	}

	public static synchronized ValidManager getInstance() {
		if(validManager == null) {
			validManager = new ValidManager();
		}

		return validManager;
	}

	public void valid(Object obj) throws BizErrorBusinessException {
		if (obj == null || isWrapClass(obj.getClass())) {
			return;
		}

		Class<?> aClass = obj.getClass();
		Field[] fields = aClass.getDeclaredFields();
		for (Field f : fields) {
			Annotation[] annotations = f.getDeclaredAnnotations();
			if (annotations == null || annotations.length <= 0) {
				continue;
			}

			for(Annotation annotation : annotations){
				if(annotation == null){
					continue;
				}
				ValidConstraint constraintValid = annotation.annotationType().getAnnotation(ValidConstraint.class);
				if(constraintValid == null){
					continue;
				}

				try {
					Class<? extends ConstraintValidator<?, ?>>  validatedBy = constraintValid.validatedBy();
					ConstraintValidator constraintValidator = validatedBy.newInstance();
					constraintValidator.initialize(annotation);

					Method method = aClass.getMethod("get" + StringUtils.toFirstUpperCase(f.getName()));
					constraintValidator.isValid(method.invoke(obj));
				} catch (InstantiationException e) {
					logger.error(e.getMessage());
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage());
				} catch (InvocationTargetException e) {
					logger.error(e.getMessage());
				} catch (NoSuchMethodException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	private Boolean isWrapClass(Class clz) {
		Boolean isWrapClass;
		try {
			isWrapClass = ((Class) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			isWrapClass = false;
		}

		return isWrapClass || String.class.getName().equals(clz.getName());
	}
}

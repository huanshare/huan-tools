package com.huanshare.tools.interceptor.annotation;

import com.huanshare.tools.interceptor.enums.PermissionTypeEnum;

import java.lang.annotation.*;

/**
 *  2018/11/15.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {
    PermissionTypeEnum type() default PermissionTypeEnum.FUNCTION;
    String code() default "";
}

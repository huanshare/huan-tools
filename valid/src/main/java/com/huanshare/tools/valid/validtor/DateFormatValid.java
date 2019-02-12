package com.huanshare.tools.valid.validtor;


import com.huanshare.tools.valid.validator.DateValidator;
import com.huanshare.tools.valid.validator.DateValidator;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ValidConstraint(validatedBy = DateValidator.class)
@Documented
public @interface DateFormatValid {
    String format() default "yyyy-MM-dd HH:mm:ss";
    String message() default "";
}

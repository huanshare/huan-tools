package com.huanshare.tools.valid.validtor;


import com.huanshare.tools.valid.validator.RegexValidator;
import com.huanshare.tools.valid.validator.RegexValidator;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ValidConstraint(validatedBy = RegexValidator.class)
@Documented
public @interface RegexValid {
    String regex() default "";
    String message() default "";
}

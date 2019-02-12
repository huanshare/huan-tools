package com.huanshare.tools.valid.validtor;


import com.huanshare.tools.valid.validator.NotNullValidator;
import com.huanshare.tools.valid.validator.NotNullValidator;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ValidConstraint(validatedBy = NotNullValidator.class)
@Documented
public @interface NotNullValid {
    String message() default "";
}

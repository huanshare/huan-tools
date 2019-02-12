package com.huanshare.tools.valid.validtor;


import com.huanshare.tools.valid.validator.NotEqualsValidator;
import com.huanshare.tools.valid.validator.NotEqualsValidator;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ValidConstraint(validatedBy = NotEqualsValidator.class)
@Documented
public @interface NotEqualsValid {
    String value() default "";
    String message() default "";
}

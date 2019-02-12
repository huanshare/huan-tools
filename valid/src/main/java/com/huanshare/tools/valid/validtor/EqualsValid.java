package com.huanshare.tools.valid.validtor;


import com.huanshare.tools.valid.validator.EqualsValidator;
import com.huanshare.tools.valid.validator.EqualsValidator;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ValidConstraint(validatedBy = EqualsValidator.class)
@Documented
public @interface EqualsValid {
    String value() default "";
    String message() default "";
}

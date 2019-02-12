package com.huanshare.tools.valid.validtor;


import com.huanshare.tools.valid.validator.MaxBigDecimalValidator;
import com.huanshare.tools.valid.validator.MaxBigDecimalValidator;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ValidConstraint(validatedBy = MaxBigDecimalValidator.class)
@Documented
public @interface MaxBigDecimalValid {
    String value() default "0";
    String message() default "";
}

package com.huanshare.tools.valid.validtor;


import com.huanshare.tools.valid.validator.MinBigDecimalValidator;
import com.huanshare.tools.valid.validator.MinBigDecimalValidator;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ValidConstraint(validatedBy = MinBigDecimalValidator.class)
@Documented
public @interface MinBigDecimalValid {
    String value() default "0";
    String message() default "";
}

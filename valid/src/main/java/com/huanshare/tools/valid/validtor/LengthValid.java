package com.huanshare.tools.valid.validtor;


import com.huanshare.tools.valid.validator.LengthValidValidator;
import com.huanshare.tools.valid.validator.LengthValidValidator;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ValidConstraint(validatedBy = LengthValidValidator.class)
@Documented
public @interface LengthValid {
    String message() default "";
    int min() default 0;
    int max() default 0;
}

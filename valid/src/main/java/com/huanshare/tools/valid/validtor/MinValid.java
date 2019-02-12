package com.huanshare.tools.valid.validtor;


import com.huanshare.tools.valid.validator.MinValidator;
import com.huanshare.tools.valid.validator.MinValidator;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ValidConstraint(validatedBy = MinValidator.class)
@Documented
public @interface MinValid {
    int value();
    String message() default "";
}

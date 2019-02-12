package com.huanshare.tools.valid.validtor;


import com.huanshare.tools.valid.validator.MaxValidator;
import com.huanshare.tools.valid.validator.MaxValidator;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@ValidConstraint(validatedBy = MaxValidator.class)
@Documented
public @interface MaxValid {
    int value();
    String message() default "";
}

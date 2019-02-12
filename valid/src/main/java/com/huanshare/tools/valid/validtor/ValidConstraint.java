package com.huanshare.tools.valid.validtor;


import com.huanshare.tools.valid.validator.ConstraintValidator;
import com.huanshare.tools.valid.validator.ConstraintValidator;

import java.lang.annotation.*;

/**
 *
 */

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidConstraint {
    Class<? extends ConstraintValidator<?, ?>> validatedBy();
}

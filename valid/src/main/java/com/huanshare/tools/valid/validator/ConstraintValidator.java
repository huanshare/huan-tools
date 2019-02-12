package com.huanshare.tools.valid.validator;

import java.lang.annotation.Annotation;

/**
 *
 */
public interface ConstraintValidator<A extends Annotation, T> {

    void initialize(A constraintAnnotation);

    boolean isValid(T value);
}

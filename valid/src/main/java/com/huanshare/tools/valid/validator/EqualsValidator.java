package com.huanshare.tools.valid.validator;

import com.huanshare.tools.valid.validtor.EqualsValid;
import com.huanshare.tools.exception.BizErrorBusinessException;
import org.apache.commons.lang3.ObjectUtils;

/**
 *
 */
public class EqualsValidator implements ConstraintValidator<EqualsValid, Object> {

    private String value;
    private String message;

    @Override
    public void initialize(EqualsValid constraintAnnotation) {
        value = constraintAnnotation.value();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }
        if (ObjectUtils.equals(this.value, value.toString())) {
            throw new BizErrorBusinessException(message);
        }
        return true;
    }
}

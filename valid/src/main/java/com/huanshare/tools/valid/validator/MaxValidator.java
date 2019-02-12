package com.huanshare.tools.valid.validator;

import com.huanshare.tools.valid.validtor.MaxValid;
import com.huanshare.tools.exception.BizErrorBusinessException;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 */
public class MaxValidator implements ConstraintValidator<MaxValid, Object> {

    private String message;
    private int maxValue;

    @Override
    public void initialize(MaxValid constraintAnnotation) {
        message = constraintAnnotation.message();
        maxValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }

        int toInt = NumberUtils.toInt(value.toString(), 0);
        if(toInt > maxValue){
            throw new BizErrorBusinessException(message);
        }
        return true;
    }
}

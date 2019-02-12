package com.huanshare.tools.valid.validator;

import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.valid.validtor.MinValid;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 */
public class MinValidator implements ConstraintValidator<MinValid, Object> {

    private String message;
    private int minValue;

    @Override
    public void initialize(MinValid constraintAnnotation) {
        message = constraintAnnotation.message();
        minValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }

        int toInt = NumberUtils.toInt(value.toString(), 0);
        if(toInt < minValue){
            throw new BizErrorBusinessException(message);
        }
        return true;
    }
}

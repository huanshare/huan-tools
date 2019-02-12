package com.huanshare.tools.valid.validator;

import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.utils.StringUtils;
import com.huanshare.tools.valid.validtor.LengthValid;

/**
 *
 */
public class LengthValidValidator implements ConstraintValidator<LengthValid, Object> {

    private String message;
    private int min;
    private int max;

    @Override
    public void initialize(LengthValid constraintAnnotation) {
        message = constraintAnnotation.message();
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }

        int valueLength = StringUtils.length(value.toString());
        if(min > 0 && valueLength <= min){
            throw new BizErrorBusinessException(message);
        }

        if(max > 0 && valueLength >= max){
            throw new BizErrorBusinessException(message);
        }

        return true;
    }
}

package com.huanshare.tools.valid.validator;

import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.utils.CommonUtils;
import com.huanshare.tools.valid.validtor.MaxBigDecimalValid;

import java.math.BigDecimal;

/**
 *
 */
public class MaxBigDecimalValidator implements ConstraintValidator<MaxBigDecimalValid, Object> {

    private String message;
    private String maxValue;

    @Override
    public void initialize(MaxBigDecimalValid constraintAnnotation) {
        message = constraintAnnotation.message();
        maxValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }

        BigDecimal toValue = CommonUtils.getBigDecimal(value.toString());
        if(toValue.compareTo(CommonUtils.getBigDecimal(maxValue)) >= 0){
            throw new BizErrorBusinessException(message);
        }
        return true;
    }
}

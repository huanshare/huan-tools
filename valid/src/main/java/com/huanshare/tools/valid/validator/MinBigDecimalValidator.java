package com.huanshare.tools.valid.validator;

import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.utils.CommonUtils;
import com.huanshare.tools.valid.validtor.MinBigDecimalValid;

import java.math.BigDecimal;

/**
 *
 */
public class MinBigDecimalValidator implements ConstraintValidator<MinBigDecimalValid, Object> {

    private String message;
    private String minValue;

    @Override
    public void initialize(MinBigDecimalValid constraintAnnotation) {
        message = constraintAnnotation.message();
        minValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }

        BigDecimal toValue = CommonUtils.getBigDecimal(value.toString());
        if(toValue.compareTo(CommonUtils.getBigDecimal(minValue)) <= 0){
            throw new BizErrorBusinessException(message);
        }
        return true;
    }
}

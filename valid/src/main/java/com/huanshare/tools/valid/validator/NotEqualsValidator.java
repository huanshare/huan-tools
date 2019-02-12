package com.huanshare.tools.valid.validator;

import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.valid.validtor.NotEqualsValid;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Created by huanshare on 2018/10/11.
 */
public class NotEqualsValidator implements ConstraintValidator<NotEqualsValid, Object> {

    private String value;
    private String message;

    @Override
    public void initialize(NotEqualsValid constraintAnnotation) {
        value = constraintAnnotation.value();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }
        if (!ObjectUtils.equals(this.value, value.toString())) {
            throw new BizErrorBusinessException(message);
        }
        return true;
    }
}

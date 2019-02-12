package com.huanshare.tools.valid.validator;

import com.huanshare.tools.valid.validtor.DateFormatValid;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.utils.DateUtils;

import java.util.Date;

/**
 *
 */
public class DateValidator implements ConstraintValidator<DateFormatValid, Object> {

    private String message;
    private String format;

    @Override
    public void initialize(DateFormatValid constraintAnnotation) {
        message = constraintAnnotation.message();
        format = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }

        Date date = DateUtils.stringToDate(value.toString(), format);
        if(date == null){
            throw new BizErrorBusinessException(message);
        }
        return true;
    }
}

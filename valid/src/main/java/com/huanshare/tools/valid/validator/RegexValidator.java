package com.huanshare.tools.valid.validator;

import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.valid.validtor.RegexValid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class RegexValidator implements ConstraintValidator<RegexValid, Object> {

    private String regex;
    private String message;

    @Override
    public void initialize(RegexValid constraintAnnotation) {
        regex = constraintAnnotation.regex();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value) {
        if(value == null){
            return true;
        }

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value.toString());
        if (!matcher.find()) {
            throw new BizErrorBusinessException(message);
        }

        return true;
    }
}

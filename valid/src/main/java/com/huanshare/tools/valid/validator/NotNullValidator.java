package com.huanshare.tools.valid.validator;

import com.huanshare.tools.valid.validtor.NotNullValid;
import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.utils.ListUtils;
import com.huanshare.tools.utils.StringUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class NotNullValidator implements ConstraintValidator<NotNullValid, Object> {

    private String message;

    @Override
    public void initialize(NotNullValid constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            throw new BizErrorBusinessException(message);
        }
        if (value instanceof String) {
            if (StringUtils.isBlank(value.toString())) {
                throw new BizErrorBusinessException(message);
            }
        } else if (value instanceof List) {
            if (ListUtils.isEmpty((List) value)) {
                throw new BizErrorBusinessException(message);
            }
        } else if (value instanceof Map) {
            if (MapUtils.isEmpty((Map) value)) {
                throw new BizErrorBusinessException(message);
            }
        }
        return true;
    }
}

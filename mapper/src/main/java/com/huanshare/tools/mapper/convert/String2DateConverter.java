package com.huanshare.tools.mapper.convert;

import com.huanshare.tools.utils.DateUtils;
import com.huanshare.tools.utils.StringUtils;
import org.modelmapper.AbstractConverter;

import java.util.Date;

/**
 *
 */
public class String2DateConverter extends AbstractConverter<String, Date> {
    @Override
    protected Date convert(String source) {
        return StringUtils.isBlank(source) ? null : DateUtils.stringToDate(source, DateUtils.FORMAT);
    }
}

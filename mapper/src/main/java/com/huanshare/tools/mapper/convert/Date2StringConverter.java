package com.huanshare.tools.mapper.convert;

import com.huanshare.tools.utils.DateUtils;
import org.modelmapper.AbstractConverter;

import java.util.Date;

/**
 *
 */
public class Date2StringConverter extends AbstractConverter<Date, String> {
    @Override
    protected String convert(Date source) {
        return source == null ? "" : DateUtils.dateToString(source);
    }
}

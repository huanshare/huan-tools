package com.huanshare.tools.mapper.convert;

import org.modelmapper.AbstractConverter;

/**
 *
 */
public class Long2StringConverter extends AbstractConverter<Long, String> {
    @Override
    protected String convert(Long source) {
        return source == null ? "" : source.toString();
    }
}

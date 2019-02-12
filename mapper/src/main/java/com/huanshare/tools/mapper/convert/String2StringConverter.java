package com.huanshare.tools.mapper.convert;

import org.modelmapper.AbstractConverter;

/**
 *
 */
public class String2StringConverter extends AbstractConverter<String, String> {
    @Override
    protected String convert(String source) {
        return source == null ? "" : source;
    }
}

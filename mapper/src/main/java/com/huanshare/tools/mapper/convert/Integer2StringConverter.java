package com.huanshare.tools.mapper.convert;

import org.modelmapper.AbstractConverter;

/**
 *
 */
public class Integer2StringConverter extends AbstractConverter<Integer, String> {
    @Override
    protected String convert(Integer source) {
        return source == null ? "" : source.toString();
    }
}

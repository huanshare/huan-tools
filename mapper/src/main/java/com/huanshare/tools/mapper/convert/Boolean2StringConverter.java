package com.huanshare.tools.mapper.convert;

import org.modelmapper.AbstractConverter;

/**
 *
 */
public class Boolean2StringConverter extends AbstractConverter<Boolean, String> {
    @Override
    protected String convert(Boolean source) {
        return source == null ? "" : source.toString();
    }
}

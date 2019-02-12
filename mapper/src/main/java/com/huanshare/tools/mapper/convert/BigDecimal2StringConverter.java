package com.huanshare.tools.mapper.convert;

import org.modelmapper.AbstractConverter;

import java.math.BigDecimal;

/**
 *
 */
public class BigDecimal2StringConverter extends AbstractConverter<BigDecimal, String> {
    @Override
    protected String convert(BigDecimal source) {
        return source == null ? "" : source.toString();
    }
}

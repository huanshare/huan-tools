package com.huanshare.tools.utils;

import java.util.List;

/**
 *  2017/6/15.
 */
public abstract class AbstractExcelLoadBean<T> {

    public abstract List<T> load() throws Exception;
}

package com.huanshare.tools.mvc.annotation.limited;

import java.lang.annotation.*;

/**
 * 加强对方法限制
 * 根据被制定方法中的维度keyName限制多少时间内允许的请求次数
 *  2018/3/31.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimitedExtFrequency {
    ExtFrequency[] limiteds();
}

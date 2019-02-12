package com.huanshare.tools.mvc.annotation.rest;

import java.lang.annotation.*;

/**
 *  2018/3/31.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ImproperChar {
    //多少时间内限制
    String regex() default "[<>]";
}

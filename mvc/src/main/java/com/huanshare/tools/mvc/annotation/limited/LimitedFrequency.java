package com.huanshare.tools.mvc.annotation.limited;

import java.lang.annotation.*;

/**
 * 根据方法名限制多少时间内允许的请求次数
 *  2018/3/31.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimitedFrequency {
    //限制次数
    int count() default 1;

    //多少时间内限制
    long timeout() default 60;

    //限制提示信息
    String message() default "";
}

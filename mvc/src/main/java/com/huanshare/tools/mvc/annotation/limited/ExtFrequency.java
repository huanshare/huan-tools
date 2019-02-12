package com.huanshare.tools.mvc.annotation.limited;

import java.lang.annotation.*;

/**
 *  2017/7/12.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExtFrequency {
    //需要幂等的keyName 有多个,当空的情况下就是全部
    String params() default "";

    //需要排除掉的keyName
    String excludeParams() default "";

    //限制次数
    int count() default 1;

    //多少时间内限制
    long timeout() default 60;

    //限制提示信息
    String message() default "";
}

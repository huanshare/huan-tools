package com.huanshare.tools.valid.validtor;

import java.lang.annotation.*;

/**
 * Created by huanshare on 2018/3/31.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamsValid {
}

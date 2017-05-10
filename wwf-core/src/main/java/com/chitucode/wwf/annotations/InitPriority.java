/**
 * Wow Web Framework
 *
 *
 * Copyright (c) 2017 Kevin Wang(Kewei Wang)
 *
 * Licensed as  MIT License
 * http://www.opensource.org/licenses/mit-license.php
 *
 * email: 827765236@qq.com
 * Date: 2017-05-10
 */
package com.chitucode.wwf.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kowaywang on 17/5/6.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InitPriority {

    int value() default 0;
}

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

import java.lang.annotation.*;

/**
 * Created by kowaywang on 17/4/13.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Path {

    String value() default "";

}

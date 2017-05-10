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

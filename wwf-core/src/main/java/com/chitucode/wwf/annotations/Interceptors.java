package com.chitucode.wwf.annotations;

import com.chitucode.wwf.interceptor.WWFInterceptor;

import java.lang.annotation.*;

/**
 * Created by kowaywang on 17/4/13.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Interceptors {

    Class<? extends WWFInterceptor>[] interceptors() default {};

}

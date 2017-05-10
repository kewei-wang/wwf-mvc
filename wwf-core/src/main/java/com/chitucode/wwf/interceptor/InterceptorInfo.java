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
package com.chitucode.wwf.interceptor;

import java.lang.reflect.Method;

/**
 * Created by kowaywang on 17/5/3.
 */
public class InterceptorInfo {

    private Object interceptorInstance;

    private Method interMethod;

    public Object getInterceptorInstance() {
        return interceptorInstance;
    }

    public void setInterceptorInstance(Object interceptorInstance) {
        this.interceptorInstance = interceptorInstance;
    }

    public Method getInterMethod() {
        return interMethod;
    }

    public void setInterMethod(Method interMethod) {
        this.interMethod = interMethod;
    }
}

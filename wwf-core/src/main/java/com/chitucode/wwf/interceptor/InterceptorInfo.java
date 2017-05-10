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

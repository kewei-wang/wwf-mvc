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
package com.chitucode.wwf.context;

import com.chitucode.wwf.interceptor.WWFInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kowaywang on 17/4/21.
 *
 * 描述到controller的方法级别,是最基本的调用单元
 *
 */
public class ActionInfo {

    private String urlPath;
    private Object controllerInstance;
    private boolean isGet;
    private boolean isPost;
    private String controllerPath;
    private String[] params;//参数名集合

    private Class<?>[] paramTypes;//参数的类型

    private Method method;

    private List<WWFInterceptor> interceptorList = new ArrayList<>();

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public Object getControllerInstance() {
        return controllerInstance;
    }

    public void setControllerInstance(Object controllerInstance) {
        this.controllerInstance = controllerInstance;
    }

    public boolean isGet() {
        return isGet;
    }

    public void setGet(boolean get) {
        isGet = get;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public List<WWFInterceptor> getInterceptorList() {
        return interceptorList;
    }

    public void setInterceptorList(List<WWFInterceptor> interceptorList) {
        this.interceptorList = interceptorList;
    }

    public String getControllerPath() {
        return controllerPath;
    }

    public void setControllerPath(String controllerPath) {
        this.controllerPath = controllerPath;
    }
}

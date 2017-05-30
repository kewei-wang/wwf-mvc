/**
 * Wow Web Framework
 * <p>
 * <p>
 * Copyright (c) 2017 Kevin Wang(Kewei Wang)
 * <p>
 * Licensed as  MIT License
 * http://www.opensource.org/licenses/mit-license.php
 * <p>
 * email: 827765236@qq.com
 * Date: 2017-05-10
 */
package com.chitucode.wwf.init.wwf;

import com.chitucode.wwf.annotations.GET;
import com.chitucode.wwf.annotations.Interceptors;
import com.chitucode.wwf.annotations.POST;
import com.chitucode.wwf.annotations.Path;
import com.chitucode.wwf.common.Global;
import com.chitucode.wwf.common.WWFConfig;
import com.chitucode.wwf.context.ActionInfo;
import com.chitucode.wwf.context.WWFContainer;
import com.chitucode.wwf.init.Init;
import com.chitucode.wwf.interceptor.WWFInterceptor;
import com.chitucode.wwf.util.ClassUtils;
import com.chitucode.wwf.util.StringUtils;
import org.apache.commons.collections.map.MultiValueMap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kowaywang on 17/4/17.
 */
public class ControllerInit implements Init {

    private static final Map<String, ActionInfo> actionInfoMap = new ConcurrentHashMap<>();

    @Override
    public void doInit(WWFConfig config) {

        String basePackage = config.getProperty("wwf.basepackage");
        String controllerPackage = basePackage + ".controllers";
        Set<Class<?>> controllerSet = ClassUtils.getClasses(controllerPackage, true);

        for (Class<?> controllerClass : controllerSet) {

            if (controllerClass.getName().endsWith("Controller")) {

                System.out.println("Controller Name Is : " + controllerClass.getName());
                String reqPath = "";
                if (ClassUtils.hasClassAnnotation(controllerClass, Path.class)) {

                    //如果在类上有Path注解,则读取这个注解的信息
                    Path classPathAnno = ClassUtils.getClassAnnotation(controllerClass, Path.class);
                    reqPath = classPathAnno.value();
                }

                //controllerMap.put(reqPath,);

                //TODO 此处为一个扩展点,可以在spring中获取
                //final Object controllerInstance = WWFContainer.getBean(aClass);
                Object controllerInstance = null;
                final WWFContainer wwfContainer = WWFConfig.getWwfContainer();

                    if (wwfContainer != null) {
                        controllerInstance = wwfContainer.getBean(controllerClass);
                    } else {
                        try {
                            controllerInstance = controllerClass.newInstance();
                        }catch (Exception e){
                            //// TODO: 17/5/13 直接退出
                        }
                    }


                    Method[] allActions = controllerClass.getDeclaredMethods();

                    List<WWFInterceptor> interceptorList = new ArrayList<>();

                    //初始化拦截器
                    if (ClassUtils.hasClassAnnotation(controllerClass, Interceptors.class)) {

                        Interceptors interceptorAnno = ClassUtils.getClassAnnotation(controllerClass, Interceptors.class);
                        Class<? extends WWFInterceptor>[] allInterceptorClazzes = interceptorAnno.interceptors();

                        for (Class<? extends WWFInterceptor> interceptor : allInterceptorClazzes) {

                            //// TODO: 17/5/6 这里可以从spring中获取

                            //WWFInterceptor interceptorInstance = WWFContainer.getBean(interceptor);
                            Object interceptorInstance = null;

                            if (wwfContainer == null) {
                                try {
                                    interceptorInstance = interceptor.newInstance();
                                }catch(Exception e){
                                    //// TODO: 17/5/13 直接退出 
                                }
                            } else {
                                interceptorInstance = wwfContainer.getBean(interceptor);
                            }
                            interceptorList.add((WWFInterceptor) interceptorInstance);

                        }


                    }

                //循环读取方法的信息进行处理

                for (Method action : allActions) {
                    try {
                        if (ClassUtils.hasMethodAnnotation(controllerClass, Path.class, action.getName(), action.getParameterTypes())) {
                            Path reqActionPathAnno = ClassUtils.getMethodAnnotation(controllerClass, Path.class, action.getName(), action.getParameterTypes());
                            String reqActionPath = reqActionPathAnno.value();

                            String completePath = reqPath + reqActionPath;
                            System.out.println(completePath);

                            //扫描到相应的URI之后,要和相应的方法对应,并存储在一个MAP里
                            if (actionInfoMap.containsKey(completePath)) {
                                //如果进入了这里,说明有两个相同的URL,这是不允许的
                                ActionInfo actionInfo = actionInfoMap.get(completePath);
                                throw new Exception("could not have the same request uri in  : [" + actionInfo.getMethod() + "] and [" + action + "]");
                            } else {

                                ActionInfo actionInfo = new ActionInfo();

                                actionInfo.setUrlPath(completePath);
                                actionInfo.setControllerInstance(controllerInstance);
                                String[] paramArr = ClassUtils.getMethodParamNames(controllerClass, action);
                                actionInfo.setParams(paramArr);
                                actionInfo.setMethod(action);
                                actionInfo.setParamTypes(action.getParameterTypes());
                                actionInfo.setControllerPath(reqPath);

                                actionInfo.setInterceptorList(interceptorList);//设置拦截器链

                                //默认情况下,只能处理get
                                actionInfo.setGet(true);
                                actionInfo.setPost(false);

                                if (ClassUtils.hasClassAnnotation(controllerClass, GET.class) ||
                                        (ClassUtils.hasMethodAnnotation(controllerClass, GET.class, action.getName(), action.getParameterTypes()) &&
                                                !ClassUtils.hasMethodAnnotation(controllerClass, POST.class, action.getName(), action.getParameterTypes()))) {
                                    //是否只处理GET请求
                                    actionInfo.setGet(true);
                                    actionInfo.setPost(false);

                                }
                                //else
                                if (ClassUtils.hasClassAnnotation(controllerClass, POST.class) ||
                                        (ClassUtils.hasMethodAnnotation(controllerClass, POST.class, action.getName(), action.getParameterTypes()) &&
                                                !ClassUtils.hasMethodAnnotation(controllerClass, GET.class, action.getName(), action.getParameterTypes()))) {
                                    //是否只处理post请求
                                    actionInfo.setGet(false);
                                    actionInfo.setPost(true);
                                }

                                //如果类上和方法上都没有get和post注解  或者 类上有get方法上有post  或者 方法上有get类上有post
                                if ((!ClassUtils.hasClassAnnotation(controllerClass, GET.class) &&
                                        !ClassUtils.hasClassAnnotation(controllerClass, POST.class) &&
                                        !ClassUtils.hasMethodAnnotation(controllerClass, GET.class, action.getName(), action.getParameterTypes()) &&
                                        !ClassUtils.hasMethodAnnotation(controllerClass, POST.class, action.getName(), action.getParameterTypes())) ||
                                        (ClassUtils.hasClassAnnotation(controllerClass, GET.class) && ClassUtils.hasMethodAnnotation(controllerClass, POST.class, action.getName(), action.getParameterTypes())) ||
                                        (ClassUtils.hasClassAnnotation(controllerClass, POST.class) && ClassUtils.hasMethodAnnotation(controllerClass, GET.class, action.getName(), action.getParameterTypes()))) {
                                    //两个都能处理
                                    actionInfo.setGet(true);
                                    actionInfo.setPost(true);
                                }

                                actionInfoMap.put(actionInfo.getUrlPath(), actionInfo);

                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(-1);//初始化失败,直接退出
                    }

                }

            }

        }


        Global.CONTROLLER_MAP = actionInfoMap;
    }
}

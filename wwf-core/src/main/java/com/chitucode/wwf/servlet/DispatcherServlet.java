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
package com.chitucode.wwf.servlet;

import com.chitucode.wwf.action.ActionResult;
import com.chitucode.wwf.annotations.*;
import com.chitucode.wwf.bind.ActionParamBinder;
import com.chitucode.wwf.bind.RequestBinder;
import com.chitucode.wwf.common.WWFConfig;
import com.chitucode.wwf.context.WWFContainer;
import com.chitucode.wwf.init.Init;
import com.chitucode.wwf.interceptor.WWFInterceptor;
import com.chitucode.wwf.context.ActionInfo;
import com.chitucode.wwf.context.BeatContext;
import com.chitucode.wwf.util.AntPathMatcher;
import com.chitucode.wwf.util.ClassUtils;
import com.chitucode.wwf.util.PathMatcher;
import com.chitucode.wwf.common.Global;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by kowaywang on 17/4/9.
 */
@WebFilter(urlPatterns = {"/*"},
        dispatcherTypes = {DispatcherType.REQUEST},
        initParams = {@WebInitParam(name = "encoding", value = "UTF-8")}
)
public class DispatcherServlet implements Filter {

    private static final Map<String, ActionInfo> actionInfoMap = new ConcurrentHashMap<>();

    private static final List<Object> initClazzList = new ArrayList<>();

    private static final PathMatcher matcher = new AntPathMatcher();

    private static ServletContext servletContext;

    private static WWFConfig wwfConfig;



    @Override
    public void init(FilterConfig config) throws ServletException {

        //先加载扩展点和插件,在进行内部初始化



        try {
            servletContext = config.getServletContext();
            //init global
            Global.SERVLET_CONTEXT_PATH = this.getServletContext().getContextPath();
            Global.VIEW_ROOT_PATH = this.getServletContext().getResource("/").getPath() + "views";

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            Properties pps = new Properties();
            ServletContext sc = this.getServletContext();
            InputStream ins = sc.getResourceAsStream("WEB-INF" + File.separator + "classes" + File.separator + "wwfconfig" + File.separator + "config.properties");
            pps.load(ins);

            Set<Class<?>> controllerSet = ClassUtils.getClasses(pps.getProperty("wwf.basepackage") + ".controllers", true);
            Set<Class<?>> initsSet = ClassUtils.getAllClassByInterface(Init.class,pps.getProperty("wwf.basepackage") + ".inits", true);

            Map<Integer,Init> initsMap = new TreeMap<>();

            //开始执行应用程序的初始化
            for(Class<?> initClazz : initsSet){
                if(ClassUtils.hasClassAnnotation(initClazz, InitPriority.class)){

                    InitPriority initPriorityAnno = initClazz.getAnnotation(InitPriority.class);
                    int value = initPriorityAnno.value();

                    if(initsMap.containsKey(value)){
                        //如果有初始化优先级相同的,则报错
                        throw new Exception("inits could not have the same priority : ["+initsMap.get(value)+"] and ["+initClazz+"]");

                    }else{
                        initsMap.put(value,(Init)initClazz.newInstance());
                    }
                }

            }
            //开始执行初始化
            for (Map.Entry<Integer,Init> initEntry: initsMap.entrySet()) {
                initEntry.getValue().doInit(wwfConfig);
            }



            //读取Controller的相关信息,进行信息提取和初始化操作
            for (Class<?> aClass : controllerSet) {

                if (aClass.getName().endsWith("Controller")) {

                    System.out.println("Controller Name Is : " + aClass.getName());
                    String reqPath = "";
                    if (ClassUtils.hasClassAnnotation(aClass, Path.class)) {

                        //如果在类上有Path注解,则读取这个注解的信息
                        Path classPathAnno = ClassUtils.getClassAnnotation(aClass, Path.class);
                        reqPath = classPathAnno.value();
                    }

                    // TODO: 17/5/5  此处构造mutilmap,以便在后面请求的时候可以优化查找速度

                    //TODO 此处为一个扩展点,可以在spring中获取
                    //final Object controllerInstance = WWFContainer.getBean(aClass);
                    final Object controllerInstance;
                    final WWFContainer wwfContainer = WWFConfig.getWwfContainer();
                    if( wwfContainer != null){
                        controllerInstance = wwfContainer.getBean(aClass);
                    }else {
                        controllerInstance = aClass.newInstance();
                    }


                    Method[] allActions = aClass.getDeclaredMethods();

                    List<WWFInterceptor> interceptorList = new ArrayList<>();

                    //初始化拦截器
                    if (ClassUtils.hasClassAnnotation(aClass, Interceptors.class)) {

                        Interceptors interceptorAnno = ClassUtils.getClassAnnotation(aClass, Interceptors.class);
                        Class<? extends WWFInterceptor>[] allInterceptorClazzes = interceptorAnno.interceptors();

                        for (Class<? extends WWFInterceptor> interceptor : allInterceptorClazzes) {

                            //// TODO: 17/5/6 这里可以从spring中获取

                            //WWFInterceptor interceptorInstance = WWFContainer.getBean(interceptor);
                            final Object interceptorInstance;

                            if(wwfContainer == null) {
                                interceptorInstance = interceptor.newInstance();
                            }else{
                                interceptorInstance = wwfContainer.getBean(interceptor);
                            }
                            interceptorList.add((WWFInterceptor)interceptorInstance);

                        }


                    }

                    //循环读取方法的信息进行处理

                    for (Method action : allActions) {
                        try {
                            if (ClassUtils.hasMethodAnnotation(aClass, Path.class, action.getName(), action.getParameterTypes())) {
                                Path reqActionPathAnno = ClassUtils.getMethodAnnotation(aClass, Path.class, action.getName(), action.getParameterTypes());
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
                                    String[] paramArr = ClassUtils.getMethodParamNames(aClass, action);
                                    actionInfo.setParams(paramArr);
                                    actionInfo.setMethod(action);
                                    actionInfo.setParamTypes(action.getParameterTypes());
                                    actionInfo.setControllerPath(reqPath);

                                    actionInfo.setInterceptorList(interceptorList);//设置拦截器链

                                    if (ClassUtils.hasClassAnnotation(aClass, GET.class) ||
                                            (ClassUtils.hasMethodAnnotation(aClass, GET.class, action.getName(), action.getParameterTypes()) &&
                                                    !ClassUtils.hasMethodAnnotation(aClass, POST.class, action.getName(), action.getParameterTypes()))) {
                                        //是否只处理GET请求
                                        actionInfo.setGet(true);
                                        actionInfo.setPost(false);

                                    }
                                    //else
                                    if (ClassUtils.hasClassAnnotation(aClass, POST.class) ||
                                            (ClassUtils.hasMethodAnnotation(aClass, POST.class, action.getName(), action.getParameterTypes()) &&
                                                    !ClassUtils.hasMethodAnnotation(aClass, GET.class, action.getName(), action.getParameterTypes()))) {
                                        //是否只处理post请求
                                        actionInfo.setGet(false);
                                        actionInfo.setPost(true);
                                    }

                                    //如果类上和方法上都没有get和post注解  或者 类上有get方法上有post  或者 方法上有get类上有post
                                    if ((!ClassUtils.hasClassAnnotation(aClass, GET.class) &&
                                            !ClassUtils.hasClassAnnotation(aClass, POST.class) &&
                                            !ClassUtils.hasMethodAnnotation(aClass, GET.class, action.getName(), action.getParameterTypes()) &&
                                            !ClassUtils.hasMethodAnnotation(aClass, POST.class, action.getName(), action.getParameterTypes())) ||
                                            (ClassUtils.hasClassAnnotation(aClass, GET.class) && ClassUtils.hasMethodAnnotation(aClass, POST.class, action.getName(), action.getParameterTypes())) ||
                                            (ClassUtils.hasClassAnnotation(aClass, POST.class) && ClassUtils.hasMethodAnnotation(aClass, GET.class, action.getName(), action.getParameterTypes()))) {
                                        //两个都能处理
                                        actionInfo.setGet(true);
                                        actionInfo.setPost(true);
                                    }

//                                        BeatContext.current().setActionInfo(actionInfo);

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


        } catch (Exception e) {

            System.out.println();
            e.printStackTrace();

        }

    }

    private ServletContext getServletContext() {
        return this.servletContext;

    }


    private void dispatchRequest(HttpServletRequest req, HttpServletResponse resp) {

        String requestUri = req.getRequestURI();
        String requestMethod = req.getMethod();


        System.out.println(requestMethod + " URI: " + requestUri);

        //通过URI找到controller实例
        String[] reqPathArr = requestUri.split("/");
        if (reqPathArr.length == 0) {
            //非法请求

        } else {

            try {
                if (matcher.match("/resources/**", requestUri)) {
                    //静态资源请求

                    //交给WEB容器处理
                    req.getRequestDispatcher(requestUri).forward(req, resp);


                } else {
                    //动态资源请求的路径

                    //TODO 此处能否优化查找时间?可以使用MutilMap进行优化
                    for (String url : actionInfoMap.keySet()) {

                        if (matcher.match(url, requestUri)) {
                            //匹配到了相应的Action
                            ActionInfo actionInfo = actionInfoMap.get(url);

                            BeatContext.current().setActionInfo(actionInfo);

                            if (!(("GET".equals(requestMethod) && actionInfo.isGet()) ||
                                    "POST".equals(requestMethod) && actionInfo.isPost() ||
                                    actionInfo.isGet() == actionInfo.isPost())) {

                                return;
                            }


                            List<WWFInterceptor> interceptorList = actionInfo.getInterceptorList();

                            //先执行拦截器链
                            for (WWFInterceptor interceptor : interceptorList) {

                                ActionResult beforeResult = interceptor.before(BeatContext.current());
                                if (beforeResult == null) continue;

                                beforeResult.renderer(); return;

                            }

                            Method method = actionInfo.getMethod();
                            Object[] param = ActionParamBinder.bind(req, actionInfo);
                            ActionResult actionResult = (ActionResult) method.invoke(actionInfo.getControllerInstance(), param);


                            //执行After拦截器链倒序执行,在渲染视图之前执行
                            for (int i = interceptorList.size() - 1; i >= 0; i--) {

                                WWFInterceptor interceptor = interceptorList.get(i);

                                ActionResult afterResult = interceptor.after(BeatContext.current(), actionResult);
                                if (afterResult == null) continue;

                                afterResult.renderer(); return;

                            }

                            //渲染视图
                            actionResult.renderer();


                            //执行complete拦截器链倒序执行,在视图渲染之后方法返回之前执行
                            for (int i = interceptorList.size() - 1; i >= 0; i--) {

                                WWFInterceptor interceptor = interceptorList.get(i);

                                ActionResult completeResult = interceptor.complete(BeatContext.current(), actionResult);
                                if (completeResult == null) continue;

                                completeResult.renderer(); return;

                            }

                            //请求结束后释放上下文资源

                            return;

                        }


                    }
                    //如果没有任何匹配的URL,说明无法发现
                    resp.sendError(404, "Sorry,No Controller or action found for path : " + req.getRequestURI());
                }

            } catch (Exception e) {
                //todo 处理所有调用过程中出现的异常,包括静态资源,拦截器,controller
                //System.out.println();
                //e.printStackTrace();
                /*StackTraceElement[] satckElements = e.getStackTrace();
                System.out.println(e.getCause());
                System.out.println(e.getMessage());*/

                e.printStackTrace();

                /*for (StackTraceElement element:satckElements) {
                    System.out.println(element);
                }*/

                try {
                    //打印到网页
                    e.printStackTrace(resp.getWriter());
                    //resp.sendError(502,e.toString());
                    //// TODO: 17/5/5 或者跳转到500页面
                }catch(Exception e1){

                }

            }finally{
                //请求结束后移除请求上下文资源
                BeatContext.destory();
            }

        }


    }


    protected void service(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        RequestBinder.bind(req, resp);

        try {
            this.dispatchRequest(req, resp);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    @Override
    public void destroy() {
        //做一些清理工作
        BeatContext.destory();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        service(servletRequest, servletResponse, filterChain);
    }
}

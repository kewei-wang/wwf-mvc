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
import com.chitucode.wwf.init.Init;
import com.chitucode.wwf.init.wwf.ControllerInit;
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

    private static  Map<String, ActionInfo> actionInfoMap = new ConcurrentHashMap<>();


    private static final PathMatcher matcher = new AntPathMatcher();

    private static ServletContext servletContext;

    private static WWFConfig wwfConfig  = new WWFConfig();



    @Override
    public void init(FilterConfig config) throws ServletException {

        //要最先初始化WWFConfig



        try {
            //先加载扩展点和插件,在进行内部初始化
           //Properties pps = new Properties();
            ServletContext sc =  config.getServletContext();  //this.getServletContext();
            InputStream ins = sc.getResourceAsStream("WEB-INF" + File.separator + "classes" + File.separator + "wwfconfig" + File.separator + "config.properties");
            wwfConfig.load(ins);
            servletContext = config.getServletContext();
            //init global
            Global.SERVLET_CONTEXT_PATH = this.getServletContext().getContextPath();
            Global.VIEW_ROOT_PATH = this.getServletContext().getResource("/").getPath() + "views";

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            Set<Class<?>> initsSet = ClassUtils.getAllClassByInterface(Init.class,wwfConfig.getProperty("wwf.basepackage") + ".inits", true);

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

            //系统组件初始化
            ControllerInit controllerInit = new ControllerInit();
            controllerInit.doInit(wwfConfig);

            System.out.println();


        actionInfoMap = Global.CONTROLLER_MAP;


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
            try {
                if (matcher.match("/resources/**", requestUri)) {
                    //静态资源请求

                    //process by application server(such as tomcat)
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

                            //execute "before" interceptors
                            if (executeBeforeInterceptors(interceptorList)) return;

                            Method method = actionInfo.getMethod();
                            Object[] param = ActionParamBinder.bind(req, actionInfo);
                            ActionResult actionResult = (ActionResult) method.invoke(actionInfo.getControllerInstance(), param);


                            //执行After拦截器链倒序执行,在渲染视图之前执行
                            if (executeAfterInterceptors(interceptorList, actionResult)) return;

                            //render the view
                            actionResult.renderer();


                            //执行complete拦截器链倒序执行,在视图渲染之后方法返回之前执行
                            if (executeCompleteInterceptors(interceptorList, actionResult)) return;

                            return;

                        }else{
                            // TODO: 17/5/13 There is no adaptable controller instance

                        }


                    }
                    //如果没有任何匹配的URL,说明无法发现
                    resp.sendError(404, "Sorry,No Controller or action found for path : " + req.getRequestURI());
                }

            } catch (Exception e) {
                //todo Process all the exceptions occured during executing,include finding static resources,execute interceptors and controllers
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

        /*}*/


    }

    /**
     * 渲染完成后执行的拦截器
     * @param interceptorList
     * @param actionResult
     * @return
     * @throws Exception
     */
    private boolean executeCompleteInterceptors(List<WWFInterceptor> interceptorList, ActionResult actionResult) throws Exception {
        for (int i = interceptorList.size() - 1; i >= 0; i--) {

            WWFInterceptor interceptor = interceptorList.get(i);

            ActionResult completeResult = interceptor.complete(BeatContext.current(), actionResult);
            if (completeResult == null) continue;

            completeResult.renderer();
            return true;

        }
        return false;
    }

    /**
     * controller执行后渲染视图之前执行
     * @param interceptorList
     * @param actionResult
     * @return
     * @throws Exception
     */
    private boolean executeAfterInterceptors(List<WWFInterceptor> interceptorList, ActionResult actionResult) throws Exception {
        for (int i = interceptorList.size() - 1; i >= 0; i--) {

            WWFInterceptor interceptor = interceptorList.get(i);

            ActionResult afterResult = interceptor.after(BeatContext.current(), actionResult);
            if (afterResult == null) continue;

            afterResult.renderer();
            return true;

        }
        return false;
    }

    /**
     * controller执行前执行的拦截器链
     * @param interceptorList
     * @return
     * @throws Exception
     */
    private boolean executeBeforeInterceptors(List<WWFInterceptor> interceptorList) throws Exception {
        for (WWFInterceptor interceptor : interceptorList) {

            ActionResult beforeResult = interceptor.before(BeatContext.current());
            if (beforeResult == null) continue;

            beforeResult.renderer();
            return true;

        }
        return false;
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

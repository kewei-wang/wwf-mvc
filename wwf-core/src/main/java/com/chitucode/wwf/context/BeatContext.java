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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kowaywang on 17/4/9.
 *
 * 封装 Controller 请求过程中的信息
 *
 */
public class BeatContext {

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected ActionInfo actionInfo;
    protected BeatContext.Model model = new Model();
    protected ServerInfo serverInfo;
    protected ClientInfo clientInfo;

    private static final ThreadLocal<BeatContext> STORE = new ThreadLocal<>();

    public BeatContext(){

    }


    /**
     * 将请求绑定到上下文环境
     * @param req
     * @param resp
     */
    public static void bind(HttpServletRequest req,HttpServletResponse resp){
        //if(current() != null) return;
        BeatContext beatContext = new BeatContext();
        beatContext.setRequest(req);
        beatContext.setResponse(resp);
        STORE.set(beatContext);
    }

    public static BeatContext current(){


        return STORE.get();

    }

    public static void destory(){
        STORE.remove();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public ActionInfo getActionInfo() {
        return actionInfo;
    }

    public void setActionInfo(ActionInfo actionInfo) {
        this.actionInfo = actionInfo;
    }

    public class Model{

        protected Map<String,Object> modelMap = new ConcurrentHashMap<>();

        public void add(String name,Object obj){
            modelMap.put(name,obj);
        }

        public Map<String,Object> toMap(){ return modelMap; }

    }

}

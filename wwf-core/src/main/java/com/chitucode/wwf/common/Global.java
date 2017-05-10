package com.chitucode.wwf.common;

import com.chitucode.wwf.context.ActionInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kowaywang on 17/4/27.
 * 全局对象(程序中使用)
 */
public class Global {

    //serverlet context
    public static String SERVLET_CONTEXT_PATH;
    //视图的目录
    public static String VIEW_ROOT_PATH;


    private static final Map<String, Object> CONTROLLER_MAP = new ConcurrentHashMap<>();

    private static final Map<String, ActionInfo> ACTION_INFO_MAP = new ConcurrentHashMap<>();


}

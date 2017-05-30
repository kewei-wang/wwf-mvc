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
package com.chitucode.wwf.common;

import com.chitucode.wwf.context.ActionInfo;
import org.apache.commons.collections.map.MultiValueMap;

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


    public static  Map<String, ActionInfo> CONTROLLER_MAP = new ConcurrentHashMap<>();

}

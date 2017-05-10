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
package com.chitucode.wwf.bind;

import com.chitucode.wwf.context.BeatContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by kowaywang on 17/4/17.
 */
public class RequestBinder {

    /**
     * 绑定请求上下文
     *
     * @param req
     * @param resp
     */
    public static void bind(HttpServletRequest req, HttpServletResponse resp){

         BeatContext.bind(req,resp);

    }

}

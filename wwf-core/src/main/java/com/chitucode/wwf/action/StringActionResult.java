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
package com.chitucode.wwf.action;

import com.chitucode.wwf.context.BeatContext;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Created by kowaywang on 17/4/17.
 */
public class StringActionResult extends ActionResult {


    private String content = "";
    private BeatContext beat = BeatContext.current();

    public StringActionResult(String content){
        if(content == null) content = "";
        this.content = content;

    }

    @Override
    public void renderer() throws Exception {
        HttpServletResponse resp = beat.getResponse();
        OutputStream os = resp.getOutputStream();//获取OutputStream输出流
        resp.setHeader("content-type", "text/html;charset=UTF-8");
        os.write(content.getBytes());
    }
}

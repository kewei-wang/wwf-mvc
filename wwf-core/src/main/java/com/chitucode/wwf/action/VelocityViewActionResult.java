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
import com.chitucode.wwf.common.Global;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.runtime.RuntimeInstance;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Properties;

/**
 * Created by kowaywang on 17/4/27.
 */
public class VelocityViewActionResult extends ActionResult{

    private BeatContext beatContext = BeatContext.current();

    private static final String viewSuffix = ".html";

    private final RuntimeInstance rtInstance;

    private static final Properties ps = new Properties();

    static{
        ps.setProperty("resource.loader", "file");
        ps.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        ps.setProperty("file.resource.loader.path", Global.VIEW_ROOT_PATH);
        ps.setProperty("file.resource.loader.cache", "false");
        ps.setProperty("file.resource.loader.modificationCheckInterval", "2");
        ps.setProperty("input.encoding", "UTF-8");
        ps.setProperty("output.encoding", "UTF-8");
        ps.setProperty("default.contentType", "text/html; charset=UTF-8");
        ps.setProperty("velocimarco.library.autoreload", "true");
        ps.setProperty("runtime.log.error.stacktrace", "false");
        ps.setProperty("runtime.log.warn.stacktrace", "false");
        ps.setProperty("runtime.log.info.stacktrace", "false");
        ps.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ps.setProperty("runtime.log.logsystem.log4j.category", "velocity_log");
    }

    public VelocityViewActionResult(String path) {
        this.viewPath = path+viewSuffix;

        rtInstance = new RuntimeInstance();

        try {
            rtInstance.init(ps);
        } catch (Exception e) {
            //throw ArgoException.raise(e);
            e.printStackTrace();
        }

    }

    private String viewPath;



    @Override
    public void renderer() throws Exception {

        Template template =  rtInstance.getTemplate(viewPath);
        HttpServletResponse response = beatContext.getResponse();
        response.setContentType("text/html;charset=\"UTF-8\"");
        response.setCharacterEncoding("UTF-8");
        // init context:
        Context context = new VelocityContext(beatContext.getModel().toMap());
        // render:
        VelocityWriter vw = null;
        try {
            vw = new VelocityWriter(response.getWriter());
            template.merge(context, vw);
            vw.flush();
        } catch (IOException e) {
            //throw ArgoException.raise(e);

        }
        finally {
            if (vw != null)
                vw.recycle(null);
        }

    }
}

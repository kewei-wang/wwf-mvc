package com.chitucode.wwf.action;

import com.chitucode.wwf.context.BeatContext;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by kowaywang on 17/5/4.
 */
public class RedirectActionResult extends ActionResult {

    private BeatContext beatContext = BeatContext.current();

    private String actionName;

    public RedirectActionResult(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public void renderer() throws Exception {
        HttpServletResponse resp = beatContext.getResponse();

        String controllerPath = beatContext.getActionInfo().getControllerPath();
        resp.sendRedirect(controllerPath+"/"+actionName);


    }
}

package com.chitucode.wwf.action;

import com.chitucode.wwf.context.BeatContext;

/**
 * Created by kowaywang on 17/4/17.
 */
public abstract class ActionResult {

    public abstract void renderer() throws Exception;

    public static void releaseBeatContext(){
        BeatContext.destory();
    }

    public static final ActionResult NULL = null;

    public static ActionResult view(String viewPath){

        return new VelocityViewActionResult(viewPath);
    }

    public static ActionResult redirectAction(String actionName){

        return new RedirectActionResult(actionName);
    }

}

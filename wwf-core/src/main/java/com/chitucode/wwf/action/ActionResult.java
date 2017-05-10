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


public abstract class ActionResult {

    public abstract void renderer() throws Exception;

    public static void releaseBeatContext() {
        BeatContext.destory();
    }

    public static final ActionResult NULL = null;

    public static ActionResult view(String viewPath) {

        return new VelocityViewActionResult(viewPath);
    }

    public static ActionResult redirectAction(String actionName) {

        return new RedirectActionResult(actionName);
    }

}

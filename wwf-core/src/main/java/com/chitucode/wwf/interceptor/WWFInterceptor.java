package com.chitucode.wwf.interceptor;

import com.chitucode.wwf.action.ActionResult;
import com.chitucode.wwf.context.BeatContext;

/**
 * Created by kowaywang on 17/5/3.
 */
public interface WWFInterceptor {

    public ActionResult before(BeatContext beatContext);

    public ActionResult after(BeatContext beatContext,ActionResult actionResult);

    public ActionResult complete(BeatContext beatContext,ActionResult actionResult);

}

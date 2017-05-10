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

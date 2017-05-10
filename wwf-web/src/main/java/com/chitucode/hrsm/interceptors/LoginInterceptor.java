package com.chitucode.hrsm.interceptors;

import com.chitucode.wwf.action.ActionResult;
import com.chitucode.wwf.interceptor.WWFInterceptor;
import com.chitucode.wwf.context.BeatContext;
import org.springframework.stereotype.Component;

/**
 * Created by kowaywang on 17/5/3.
 */
@Component
public class LoginInterceptor implements WWFInterceptor {
    @Override
    public ActionResult before(BeatContext beatContext) {

        System.out.println("LoginInterceptor.before");

        return null;
    }

    @Override
    public ActionResult after(BeatContext beatContext, ActionResult actionResult) {

        System.out.println("LoginInterceptor.after");

        return null;
    }

    @Override
    public ActionResult complete(BeatContext beatContext, ActionResult actionResult) {

        System.out.println("LoginInterceptor.complete");

        return null;
    }
}

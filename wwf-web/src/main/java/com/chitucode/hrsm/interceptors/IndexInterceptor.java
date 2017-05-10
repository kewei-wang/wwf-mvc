package com.chitucode.hrsm.interceptors;

import com.chitucode.hrsm.service.IHelloService;
import com.chitucode.wwf.action.ActionResult;
import com.chitucode.wwf.interceptor.WWFInterceptor;
import com.chitucode.wwf.context.BeatContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by kowaywang on 17/5/3.
 */
@Component
public class IndexInterceptor implements WWFInterceptor {


    @Resource
    IHelloService helloService;

    @Override
    public ActionResult before(BeatContext beatContext) {

        System.out.println("IndexInterceptor.before");

        System.out.println(helloService.sayHello("koway")+"=-=-=-=-=-=-=-=-=-=-=-==-===-=-=-=-");

        return null;
    }

    @Override
    public ActionResult after(BeatContext beatContext, ActionResult actionResult) {

        System.out.println("IndexInterceptor.after");

        return null;
    }

    @Override
    public ActionResult complete(BeatContext beatContext, ActionResult actionResult) {

        System.out.println("IndexInterceptor.complete");

        return null;
    }
}

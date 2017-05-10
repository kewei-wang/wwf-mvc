package com.chitucode.hrsm.controllers;

import com.chitucode.hrsm.entity.Person;
import com.chitucode.hrsm.service.IHelloService;
import com.chitucode.wwf.action.ActionResult;
import com.chitucode.wwf.action.StringActionResult;
import com.chitucode.wwf.action.VelocityViewActionResult;
import com.chitucode.wwf.annotations.GET;
import com.chitucode.wwf.annotations.Path;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * Created by kowaywang on 17/4/10.
 */
@Path("/index")
@Controller
public class IndexController extends BaseController {

    @Resource
    IHelloService helloService;

    @Path("/toList/{userName}/id/{id:\\d+}")
    public ActionResult toList(String userName, Long id, Person person){

        String kname = beat().getRequest().getParameter("kname");

        kname += "wwwwwwww---wwww--"+userName+"The id is : "+id;
        return new StringActionResult(kname);
    }

    @Path("/toAdd")
    @GET
    public ActionResult toAdd(){


        return new StringActionResult("add");
    }

    @Path("/toNews")
    public ActionResult toNews() throws Exception {

        beat().getModel().add("hello","hahhahajjjjj");
        beat().getModel().add("threadName",Thread.currentThread().getId());

        if(1 == 1) {
            throw new Exception("exception test");
        }

        return new VelocityViewActionResult("/hello/haha");
    }

    @Path("/doDelete/{id:\\d+}")
    public ActionResult doDelete(Long id){

        return new StringActionResult("");
    }

    @Path("/red")
    public ActionResult red(){

        return ActionResult.redirectAction("toAdd");
    }

    @Path("/sayHello/{name}")
    public ActionResult sayHello(String name){

        String respStr = helloService.sayHello(name);

        return new StringActionResult(respStr);
    }


}

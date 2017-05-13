package com.chitucode.hrsm.controllers;

import com.chitucode.wwf.action.ActionResult;
import com.chitucode.wwf.action.StringActionResult;
import com.chitucode.wwf.annotations.Path;
import org.springframework.stereotype.Controller;

/**
 * Created by kowaywang on 17/4/10.
 */
@Controller
public class LogController extends BaseController {

    @Path("/{name}")
    public ActionResult doLog(String name){


        return new StringActionResult("oooooo");
    }

    /**
     * 这个是一个比较复杂的例子，
     * Path中的路径可以用正则表达式匹配，
     * @Path("{phoneNumber:\\d+}")和@Path("{name}")的匹配顺序是
     * 如果都匹配，先匹配模板路径长的也就是@Path("{phoneNumber:\\d+}")
     *
     * @param phoneNumber
     * @return
     */
    @Path("/{phoneNumber:\\d+}")
    public ActionResult doTT(Long phoneNumber){

        return new StringActionResult("what");
    }

    @Path("/")
    public ActionResult iii(){

        return new StringActionResult("aaaaccc");
    }

}

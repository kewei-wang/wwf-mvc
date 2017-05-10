package com.chitucode.hrsm.controllers;

import com.chitucode.wwf.action.ActionResult;
import com.chitucode.wwf.action.StringActionResult;
import com.chitucode.wwf.annotations.Path;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.springframework.stereotype.Controller;

/**
 * Created by kowaywang on 17/4/10.
 */
@Path("/user")
@Controller
public class UserController extends BaseController{

    @Path("/toUserList")
    public ActionResult toList(){

        return new StringActionResult("index");
    }

}

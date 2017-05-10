package com.chitucode.hrsm.controllers.config;

import com.chitucode.hrsm.controllers.BaseController;
import com.chitucode.wwf.action.ActionResult;
import com.chitucode.wwf.action.StringActionResult;
import com.chitucode.wwf.annotations.Path;
import org.springframework.stereotype.Controller;

/**
 * Created by kowaywang on 17/4/16.
 */
@Path("/config")
@Controller
public class ConfigController extends BaseController{

    @Path("/toList")
    public ActionResult toList(){

        return new StringActionResult("toList");
    }

}

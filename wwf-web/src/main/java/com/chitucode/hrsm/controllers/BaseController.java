package com.chitucode.hrsm.controllers;

import com.chitucode.hrsm.interceptors.IndexInterceptor;
import com.chitucode.hrsm.interceptors.LoginInterceptor;
import com.chitucode.wwf.WWFController;
import com.chitucode.wwf.annotations.Interceptors;
import org.springframework.stereotype.Controller;

/**
 * Created by kowaywang on 17/5/3.
 */
@Interceptors(interceptors = {IndexInterceptor.class, LoginInterceptor.class})
public class BaseController extends WWFController{

}

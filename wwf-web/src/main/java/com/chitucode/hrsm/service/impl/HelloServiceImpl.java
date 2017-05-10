package com.chitucode.hrsm.service.impl;

import com.chitucode.hrsm.service.IHelloService;
import org.springframework.stereotype.Service;

/**
 * Created by kowaywang on 17/5/6.
 */
@Service
public class HelloServiceImpl implements IHelloService {

    @Override
    public String sayHello(String name) {
        return "Hello "+name;
    }
}

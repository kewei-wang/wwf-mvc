package com.chitucode.wwf;

import com.chitucode.wwf.context.BeatContext;

/**
 * Created by kowaywang on 17/4/17.
 */

public class WWFController {

    protected BeatContext beat(){

        return BeatContext.current();

    }

}

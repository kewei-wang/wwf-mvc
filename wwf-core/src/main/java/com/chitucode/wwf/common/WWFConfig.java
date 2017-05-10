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
package com.chitucode.wwf.common;

import com.chitucode.wwf.context.WWFContainer;

/**
 * Created by kowaywang on 17/5/6.
 */
public class WWFConfig {

    private static WWFContainer wwfContainer = null;

    public static WWFContainer getWwfContainer() {
        return wwfContainer;
    }

    public static void setWwfContainer(WWFContainer wwfContainer) {
        WWFConfig.wwfContainer = wwfContainer;
    }
}

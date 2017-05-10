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

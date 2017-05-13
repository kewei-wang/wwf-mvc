package com.chitucode;

import com.chitucode.wwf.context.ActionInfo;
import org.apache.commons.collections.map.MultiValueMap;
import org.junit.Test;

import java.util.Collection;


/**
 * Created by kowaywang on 17/5/13.
 */
public class TestMutilMap {

    @Test
    public void testMutilMap(){

        MultiValueMap map = new MultiValueMap();

        map.put("www",1);
        map.put("www",2);
        map.put("aa",3);

        Collection c = map.getCollection("www");

        for(Object o : c){

            System.out.println(o);

        }

        System.out.println(map.get("www"));




    }

}

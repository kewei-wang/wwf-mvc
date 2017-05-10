package com.chitucode;


import com.chitucode.entity.Student;
import com.chitucode.wwf.util.AntPathMatcher;
import com.chitucode.wwf.util.ClassUtils;
import com.chitucode.wwf.util.PathMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

/**
 * Unit test for simple App.
 */
public class AppTest {

    String requestPath="/user/toList?username=aaa&departmentid=2&pageNumber=1&pageSize=20";//请求路径
    String reqPath2 = "/user/{userId:\\d+}";
    String reqPath3 = "/user/{userName}";
    String patternPath="/user/1";//路径匹配模式

    String staticResourcePattern = "/resources/**";
    String getStaticResourceReq = "/resources/html/haha.html";

    PathMatcher matcher = new AntPathMatcher();

    @Test
    public void testAntPathMatcher(){


        boolean b = matcher.match(reqPath3, patternPath);

        Map<String,String> matchedMap = matcher.extractUriTemplateVariables(reqPath2,patternPath);

        System.out.println(matchedMap);

        Assert.assertEquals(b,true);

    }

    @Test
    public void testGetFieldName(){

        Field[] fields = Student.class.getDeclaredFields();
        for(Field f : fields){

            System.out.println(f.getName());

        }

    }

    @Test
    public void testGetParamList(){

        /*String[] paramArr = ClassUtils.getMethodParamNames(Student.class,"action");

        for (String param:paramArr) {

            System.out.println(param);

        }*/

    }

    @Test
    public void testStaticReq(){

        boolean b = matcher.match(staticResourcePattern, getStaticResourceReq);

        Assert.assertEquals(b,true);
    }

    @Test
    public void testTreeMap(){

        Map<Integer,String> map = new TreeMap<>();

        map.put(1,"first");
        map.put(3,"third");
        map.put(2,"second");
        map.put(0,"zero");

        System.out.println(map);

    }


}

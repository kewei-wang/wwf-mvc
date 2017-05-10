package com.chitucode.hrsm.inits;

import com.chitucode.wwf.annotations.InitPriority;
import com.chitucode.wwf.common.WWFConfig;
import com.chitucode.wwf.context.WWFContainer;
import com.chitucode.wwf.init.Init;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by kowaywang on 17/5/6.
 *
 */
@InitPriority(0)
public class SpringInit implements Init{

    private ApplicationContext applicationContext;

    @Override
    public void doInit(WWFConfig config) {

        applicationContext = new ClassPathXmlApplicationContext("classpath*:/spring/*.xml");

        config.setWwfContainer(new SpringWWFContainer());
        //后面获取的所有的bean都是已经被注入的
        //applicationContext.getBean();

    }

    /**
     * spring 和 WWF的桥梁
     * 在WWF中,容器是中立的,如果想让spring接管容器,则需要将spring的容器getBean转换成WWFContainer的getBean。
     * WWFContainer没有IOC的功能,仅仅只是个接口,没有默认实现,因此这也是一个扩展点。
     */
    private class SpringWWFContainer implements WWFContainer{

        @Override
        public Object getBean(Class<?> clazz) {

            Object result = new Object();
            try {
                result = applicationContext.getBean(clazz);
                return result;
            }catch(NoUniqueBeanDefinitionException e){

            }

            try {
                result = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return result;
        }
    }
}

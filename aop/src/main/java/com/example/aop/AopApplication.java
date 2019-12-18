package com.example.aop;

import com.example.aop.LogService;
import com.example.aop.Test1Service;
import com.example.aop.aspect.Advice;
import com.example.aop.aspect.Aspect;
import com.example.aop.aspect.IocContainer;
import com.example.aop.aspect.Pointcut;


public class AopApplication {

    public static void main(String[] args) {
        try {
            Advice advice = new LogService();
            Pointcut pointcut = new Pointcut("com\\.example\\.aop\\..*", ".*Message");
            Aspect aspect = new Aspect(advice, pointcut);

            IocContainer iocContainer = new IocContainer();
            iocContainer.addBeanDefinition("test1", Test1Service.class);
            iocContainer.setAspect(aspect);

            TestSerice test1Service = (TestSerice) iocContainer.getBean("test1");
            test1Service.fullMessage("111111111");
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}

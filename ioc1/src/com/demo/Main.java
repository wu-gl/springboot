package com.demo;

import com.demo.ioc.impl.DefaultBeanFactory;
import com.demo.ioc.impl.GenericBeanDefinition;

public class Main {

    static DefaultBeanFactory factory = new DefaultBeanFactory();
    public static void main(String[] args) throws Exception {
	// write your code here
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(TeacherBean.class);
//        bd.setScope(HkBeanDefinition.PROTOTYPE);
        bd.setInitMethodName("init");
        factory.registerBeanDefinition("teacher", bd);


        TeacherBean t =(TeacherBean) factory.getBean("teacher");
        TeacherBean t1 =(TeacherBean) factory.getBean("teacher");
        t.teach();
        t1.teach();
        System.out.println(t==t1);
    }
}


package com.demo.ioc;

/**
 * @Description Bean对象的定义
 * @Author 吴桂林
 * @Date 2020/3/2 14:14
 * @Version 1.0
 */
public interface MyBeanDefinition {

    final static String SINGLETION = "singleton";

    final static String PROTOTYPE = "prototype";

    Class<?> getBeanClass();

    String getScope();

    boolean isSingleton();

    boolean isPrototype();

    String getInitMethodName();

}

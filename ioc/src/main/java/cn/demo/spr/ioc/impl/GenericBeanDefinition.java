package cn.demo.spr.ioc.impl;

import cn.demo.spr.ioc.MyBeanDefinition;

import java.util.Objects;

/**
 * @Description GenericBeanDefinition
 * @Author 吴桂林
 * @Date 2020/3/3 16:27
 * @Version 1.0
 */
public class GenericBeanDefinition implements MyBeanDefinition {

    private Class<?> beanClass;

    private String scope = MyBeanDefinition.SINGLETION;

    private String initMethodName;


    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }


    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public boolean isSingleton() {
        return Objects.equals(scope, MyBeanDefinition.SINGLETION);
    }

    @Override
    public boolean isPrototype() {
        return Objects.equals(scope, MyBeanDefinition.PROTOTYPE);
    }

    @Override
    public String getInitMethodName() {
        return initMethodName;
    }
}

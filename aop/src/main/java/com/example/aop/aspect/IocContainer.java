package com.example.aop.aspect;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;


/**
 * @Description IocContainer
 * @Author 吴桂林
 * @Date 2019/12/18 16:10
 * @Version 1.0
 */
public class IocContainer {

    public Map<String, Class<?>> beanDefintionMap = new HashMap<>();

    private Aspect aspect;

    public void addBeanDefinition(String beanName, Class<?> beanClass) {
        this.beanDefintionMap.put(beanName, beanClass);
    }


    //行为？
    public Object getBean(String beanName) throws Exception {
        Object bean = createInstance(beanName);
        bean = proxyEnhance(bean);
        return bean;
    }

    private Object proxyEnhance(Object bean) {
        if (bean.getClass().getName().matches(this.aspect.getPointcut().getClassPattern())) {
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), bean.getClass().getInterfaces(), new AopInvocationHandler(bean, aspect));
        }
        return bean;
    }

    private Object createInstance(String beanName) throws Exception {
        return this.beanDefintionMap.get(beanName).newInstance();
    }

    public Aspect getAspect() {
        return aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }
}

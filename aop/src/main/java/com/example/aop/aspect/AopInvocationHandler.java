package com.example.aop.aspect;

import com.example.aop.aspect.Aspect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Description LogService
 * @Author 吴桂林
 * @Date 2019/12/18 15:48
 * @Version 1.0
 */
public class AopInvocationHandler implements InvocationHandler {

    private Aspect aspect;
    private Object target;

    public AopInvocationHandler(Object target, Aspect aspect) {
        super();
        this.aspect = aspect;
        this.target = target;
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        if (method.getName().matches(this.aspect.getPointcut().getMethodPattern())) {
            return this.aspect.getAdvice().invoke(target, method, args);
        }
        return method.invoke(target, args);
    }
}

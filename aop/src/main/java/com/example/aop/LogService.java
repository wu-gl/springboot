package com.example.aop;

import com.example.aop.aspect.Advice;

import java.lang.reflect.Method;

/**
 * @Description LogService
 * @Author 吴桂林
 * @Date 2019/12/18 15:48
 * @Version 1.0
 */
public class LogService implements Advice {
    @Override
    public Object invoke(Object target, Method method, Object[] args) throws Exception {
        long stime = System.currentTimeMillis();
        Object ret = method.invoke(target, args);
        long useTime = System.currentTimeMillis() - stime;
        System.out.println(String.format("耗时%s毫秒", useTime));
        return ret;
    }
}

package com.example.aop.aspect;

import java.lang.reflect.Method;

/**
 * @Description 需要增强的功能,通知
 * @Author 吴桂林
 * @Date 2019/12/18 15:45
 * @Version 1.0
 */
public interface Advice {
    /**
     * 定义一个方法
     * 用户在此提供增强方法
     * 执行前记录时间
     * 执行增强方法
     * 获取结束时间
     * @param target 目标类
     * @param method 方法
     * @param args 参数
     */
    Object invoke(Object target, Method method, Object[] args) throws Exception;
}

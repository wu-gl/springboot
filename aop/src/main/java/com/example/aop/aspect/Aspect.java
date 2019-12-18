package com.example.aop.aspect;

import com.example.aop.aspect.Advice;
import com.example.aop.aspect.Pointcut;

/**
 * @Description Aspect
 * @Author 吴桂林
 * @Date 2019/12/18 16:00
 * @Version 1.0
 */
public class Aspect {

    public Aspect(Advice advice, Pointcut pointcut) {
        this.advice = advice;
        this.pointcut = pointcut;
    }

    private Advice advice;
    private Pointcut pointcut;

    public Advice getAdvice() {
        return advice;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }
}

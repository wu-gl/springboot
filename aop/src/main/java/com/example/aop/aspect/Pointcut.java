package com.example.aop.aspect;

/**
 * @Description 准备增强的方法
 * @Author 吴桂林
 * @Date 2019/12/18 15:51
 * @Version 1.0
 */

public class Pointcut {
    //类名匹配规则（正则表达式）
    private String classPattern;

    //方法匹配规则（正则表达式）
    private String methodPattern;

    public Pointcut(String classPattern, String methodPattern) {
        this.classPattern = classPattern;
        this.methodPattern = methodPattern;
    }

    public String getClassPattern() {
        return classPattern;
    }

    public void setClassPattern(String classPattern) {
        this.classPattern = classPattern;
    }

    public String getMethodPattern() {
        return methodPattern;
    }

    public void setMethodPattern(String methodPattern) {
        this.methodPattern = methodPattern;
    }
}

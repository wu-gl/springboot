package com.demo;

/**
 * @Description TeacherBean
 * @Author 吴桂林
 * @Date 2020/3/3 16:31
 * @Version 1.0
 */
public class TeacherBean {

    public void teach(){
        System.out.println(this+"执行了teach方法，老师要开始上课了！");
    }

    public void init(){
        System.out.println("Teacher类的初始化init方法被执行了");
    }

}

package cn.demo.spr.di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
/**@interface 表示注解类型*/
public @interface MyComponent {
    /**为此注解定义scope属性*/
    public String scope() default "";
}
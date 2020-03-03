package cn.demo.spr.ioc;

/**
 * @Description Bean注册
 * @Author 吴桂林
 * @Date 2020/3/3 16:25
 * @Version 1.0
 */
public interface MyBeanDefinitionRegistry {

    void registerBeanDefinition (String beanName, MyBeanDefinition myBeanDefinition) throws Exception;

    MyBeanDefinition getBeanDefinition(String beanName);

    boolean containsBeanDefinition(String beanName);

}

package cn.demo.spr.ioc;

/**
 * @Description Bean工厂，获取bean对象
 * @Author 吴桂林
 * @Date 2020/3/2 14:13
 * @Version 1.0
 */
public interface MyBeanFactory {
    Object getBean(String name) throws Exception;
}

package com.demo.di;

import com.demo.TeacherBean;
import com.demo.di.interf.MyComponent;
import com.demo.ioc.impl.DefaultBeanFactory;
import com.demo.ioc.impl.GenericBeanDefinition;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;

/**
 * @Description AnnotationConfigApplicationContext
 * @Author 吴桂林
 * @Date 2020/3/3 16:51
 * @Version 1.0
 */
public class AnnotationConfigApplicationContext {
    DefaultBeanFactory factory = new DefaultBeanFactory();

    /**
     * 有参构造方法,参数类型为指定要扫描加载的包名
     */
    public AnnotationConfigApplicationContext(String packageName) {
        /**扫描指定的包路径*/
        scanPkg(packageName);
    }

    /**
     * 扫描指定包,找到包中的类文件。
     * 对于标准(类上有定义注解的)类文件反射加载创建类定义对象并放入容器中
     */
    private void scanPkg(final String pkg) {
        //替换包名中的".",将包结构转换为目录结构
        String pkgDir = pkg.replaceAll("\\.", "/");
        //获取目录结构在类路径中的位置(其中url中封装了具体资源的路径)
        URL url = getClass().getClassLoader().getResource(pkgDir);
        //基于这个路径资源(url),构建一个文件对象
        File file = new File(url.getFile());
        //获取此目录中指定标准(以".class"结尾)的文件
        File[] fs = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                //获取文件名
                String fName = file.getName();
                //判断该文件是否为目录，如为目录，递归进一步扫描其内部所有文件
                if (file.isDirectory()) {
                    scanPkg(pkg + "." + fName);
                } else {
                    //判定文件的后缀是否为.class
                    if (fName.endsWith(".class")) {
                        return true;
                    }
                }
                return false;
            }
        });
        //遍历所有符合标准的File文件
        for (File f : fs) {
            //获取文件名
            String fName = f.getName();
            //获取去除.class之后的文件名
            fName = fName.substring(0, fName.lastIndexOf("."));
            //将名字(类名,通常为大写开头)的第一个字母转换小写(用它作为key存储工厂中)
            String key = String.valueOf(fName.charAt(0)).toLowerCase() + fName.substring(1);
            //构建一个类全名(包名.类名)
            String pkgCls = pkg + "." + fName;
            try {
                //通过反射构建类对象
                Class<?> c = Class.forName(pkgCls);
                //判定这个类上是否有MyComponent注解
                if (c.isAnnotationPresent(MyComponent.class)) {
                    //将类对象存储到map容器中
                    GenericBeanDefinition bd = new GenericBeanDefinition();
                    bd.setBeanClass(c);
                    //bd.setScope(HkBeanDefinition.PROTOTYPE);
                    bd.setInitMethodName("init");
                    factory.registerBeanDefinition(key, bd);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

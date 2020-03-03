package cn.demo.spr.mv;

import cn.demo.spr.di.AnnotationConfigApplicationContext;
import cn.demo.spr.di.annotation.MyAutowired;
import cn.demo.spr.ioc.impl.DefaultBeanFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MyServlet extends HttpServlet {

    // todo 这个可以写在web.xml中
    private static final String packageNames = "cn.demo.spr.controller";
    private static final String servicePackageNames = "cn.demo.spr.services";
    DefaultBeanFactory factory = new DefaultBeanFactory();

    // 根据类名找controller
    private Map<String, Class> iocMap = new HashMap<>(64);
    //根据url找处理的方法
    private Map<String, Method> handlerMapping = new HashMap<>(64);
    //根据url找到处理的类 反射调用方法的时候需要用到
    private Map<String, Object> controllerMap = new HashMap<>(64);


    @Override
    public void init() throws ServletException {
        try {
            //扫描所有的包，将带有controller注解的放到ioc容器里待用
            scanPackages();

            //把ioc容器里的controller类遍历找到myMapping注解的方法，拼url，初始化handlerMapping和controllerMap
            //这一步就可以把访问的路径和处理方法对应起来
            dealHandlerMapping();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPut(req, resp);
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            dispatch(req, resp);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");


        if (!handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 !!! 服务器没有这个页面哦");
            return;
        }

        Method method = handlerMapping.get(url);

        //方法的参数类型
        Class[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();


        //从请求中获取参数列表  key是参数名，value是对象
        Map<String, String[]> parameterMap = req.getParameterMap();

        //方法反射调用需要的参数 Object[]  里面放从请求过来的参数
        Object[] methodParameter = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            //获取方法名
            String parameterType = parameterTypes[i].getSimpleName();

            if (parameterType.equals("HttpServletRequest")) {
                methodParameter[i] = req;
                continue;
            }
            if (parameterType.equals("HttpServletResponse")) {
                methodParameter[i] = resp;
                continue;
            }
            //todo 简单处理String类型的
            if (parameterType instanceof String) {
                //获取参数的注解
                MyParam myParam = parameters[i].getAnnotation(MyParam.class);
                //获取到请求的参数，是个数组类型的，我一查原来是防止参数重名的
                String[] requestParam = parameterMap.get(myParam.value());

                if (requestParam != null && requestParam.length > 0) {
                    //简单的将数组转换为String然后加到反射用的方法参数数组里
                    methodParameter[i] = Arrays.toString(requestParam).replaceAll("[\\[\\]]", "")
                            .replaceAll(",", "");
                }
            }
        }

        Object d = method.invoke(controllerMap.get(url), methodParameter);
        if (d != null) {
            //todo 要根据实际类型处理，简化处理
            if (d instanceof String) {
                resp.getWriter().write(d.toString());
            }
        }
    }

    private void dealHandlerMapping() throws Exception {
        if (iocMap.isEmpty()) return;

        for (Map.Entry<String, Class> entry : iocMap.entrySet()) {
            Class controllerClazz = entry.getValue();
            //接下来拼url
            String requestUrl = "";
            if (controllerClazz.isAnnotationPresent(MyMapping.class)) {
                MyMapping myMapping = (MyMapping) controllerClazz.getAnnotation(MyMapping.class);
                requestUrl = myMapping.value();
            }
            Method[] controllerMethods = controllerClazz.getMethods();
            for (Method method : controllerMethods) {
                if (method.isAnnotationPresent(MyMapping.class)) {
                    String url = requestUrl + method.getAnnotation(MyMapping.class).value();
                    handlerMapping.put(url, method);
                    controllerMap.put(url, controllerClazz.newInstance());

                    //通过反射构建类对象
                    Field[] fields = controllerClazz.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(MyAutowired.class)) {
                            field.setAccessible(true);
                            Object o = factory.getBean(field.getName());
                            field.set(controllerMap.get(url), o);
                        }
                    }
                }
            }
        }
    }

    private void scanPackages() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        String packagePath = this.getClass().getResource("/" + packageNames.replaceAll("\\.", "/")).getPath();

        findClassWithAnnotation(packagePath);


        new AnnotationConfigApplicationContext(servicePackageNames);
    }

    private void findClassWithAnnotation(String filePath) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                findClassWithAnnotation(f.getPath());
            } else {
                //通过反射实例化类，放到map里面，key是类名
                putClass2IOCMap(f);
            }
        }
    }

    private void putClass2IOCMap(File f) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class clazz = Class.forName(transferPath2PackagePath(f));
        if (clazz != null && clazz.isAnnotationPresent(MyController.class)) {
            iocMap.put(clazz.getSimpleName(), clazz);
        }
    }

    private String transferPath2PackagePath(File f) {
        return packageNames + "." + f.getName().replace(".class", "");
    }
}

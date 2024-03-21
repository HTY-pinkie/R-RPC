package com.wb20.rrpc.demoproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * java.lang.reflect.InvocationHandler
 * Object invoke(Object proxy, Method method, Object[] args) 定义了代理对象调用方法时希望执行的动作，用于集中处理在动态代理类对象上的方法调用
 * java.lang.reflect.Proxy
 * static InvocationHandler getInvocationHandler(Object proxy)  用于获取指定代理对象所关联的调用处理器
 * static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces) 返回指定接口的代理类
 * static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) 构造实现指定接口的代理类的一个新实例，所有方法会调用给定处理器对象的 invoke 方法
 * static boolean isProxyClass(Class<?> cl) 返回 cl 是否为一个代理类
 */
public class Client2 {
        public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        // 设置变量可以保存动态代理类，默认名称以 $Proxy0 格式命名
        // System.getProperties().setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        // 1. 创建被代理的对象，AdminService接口的实现类
        AdminServiceImpl AdminServiceImpl = new AdminServiceImpl();
        // 2. 获取对应的 ClassLoader
        ClassLoader classLoader = AdminServiceImpl.getClass().getClassLoader();
        // 3. 获取所有接口的Class，这里的AdminServiceImpl只实现了一个接口AdminService，
        Class[] interfaces = AdminServiceImpl.getClass().getInterfaces();
        // 4. 创建一个将传给代理类的调用请求处理器，处理所有的代理对象上的方法调用
        //     这里创建的是一个自定义的日志处理器，须传入实际的执行对象 AdminServiceImpl
        InvocationHandler logHandler = new LogHandler(AdminServiceImpl);
        /*
		   5.根据上面提供的信息，创建代理对象 在这个过程中，
               a.JDK会通过根据传入的参数信息动态地在内存中创建和.class 文件等同的字节码
               b.然后根据相应的字节码转换成对应的class，
               c.然后调用newInstance()创建代理实例
		 */
        AdminService proxy = (AdminService) Proxy.newProxyInstance(classLoader, interfaces, logHandler);
        // 调用代理的方法
        proxy.select();
        proxy.update();

        // 保存JDK动态代理生成的代理类，类名保存为 AdminServiceProxy
//        ProxyUtils.generateClassFile(AdminServiceImpl.getClass(), "AdminServiceProxy");
    }
}

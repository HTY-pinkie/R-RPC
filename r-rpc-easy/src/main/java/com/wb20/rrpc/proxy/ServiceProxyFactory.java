package com.wb20.rrpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂（用于创建代理对象）
 */
public class ServiceProxyFactory {

    //返回对应服务的代理服务
    public static <T> T getProxy(Class<T> serviceClass) {
        //new ServiceProxy()这个返回了data也就是user，
        //返回指定接口的`代理类`的实例，该接口将方法调用分派给指定的调用处理程序。
        return (T) Proxy.newProxyInstance(
                //serviceClass.getClassLoader() 部分用于获取指定服务接口 serviceClass 所使用的类加载器。这个类加载器通常是加载定义接口的类的类加载器
                serviceClass.getClassLoader(),
                //new Class[]{serviceClass} 用于指定代理对象所要实现的接口列表，而在本例中，它仅包含了一个元素，即通过参数传递进来的 serviceClass 接口
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}

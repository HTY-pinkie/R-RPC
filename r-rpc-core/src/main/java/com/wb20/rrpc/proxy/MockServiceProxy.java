package com.wb20.rrpc.proxy;


import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理（JDK动态代理）
 * 模拟远程服务
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    /**
     *
     * @param proxy 调用该方法的代理实例
     *
     * @param method {@code Method}实例对应于在代理实例上调用的接口方法。{@code Method}对象的声明类将是该方法被声明的接口，该接口可能是代理类继承该方法所通过的代理接口的超接口。
     *
     * @param args:一个对象数组，其中包含在代理实例的方法调用中传递的参数值，如果接口方法不接受参数，则为{@code null}。基本类型的参数被包装在适当的基本包装器类的实例中，例如{@code java.lang.Integer}或{@code java.lang.Boolean}。
     *
     * 有几个方法调用几次
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("动态代理调用mock");
        //根据方法的返回值类型，生成特定的默认值对象
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    private Object getDefaultObject(Class<?> type) {
        //基本类型
        if(type.isPrimitive()) {
            if(type == boolean.class) {
                return false;
            } else if(type == short.class) {
                return (short) 0;
            } else if (type == int.class) {
                return 0;
            } else if (type == long.class) {
                return 0L;
            }
        }

        //对象类型
        return null;
    }
}

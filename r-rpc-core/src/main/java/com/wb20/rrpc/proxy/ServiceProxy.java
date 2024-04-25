package com.wb20.rrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.wb20.rrpc.RpcApplication;
import com.wb20.rrpc.config.RpcConfig;
import com.wb20.rrpc.constant.RpcConstant;
import com.wb20.rrpc.model.RpcRequest;
import com.wb20.rrpc.model.RpcResponse;
import com.wb20.rrpc.model.ServiceMetaInfo;
import com.wb20.rrpc.protocol.*;
import com.wb20.rrpc.registry.Registry;
import com.wb20.rrpc.registry.RegistryFactory;
import com.wb20.rrpc.serializer.JdkSerializer;
import com.wb20.rrpc.serializer.Serializer;
import com.wb20.rrpc.serializer.SerializerFactory;
import com.wb20.rrpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {
    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    //在 Java 的动态代理中，这三个参数是由 Java 运行时系统自动传递的。
    //proxy 参数表示代理对象本身，即通过动态代理生成的代理对象。在 invoke 方法中，通常不需要直接使用这个参数。
    //method 参数表示被调用的方法对象，即客户端代码调用代理对象的哪个方法。在 invoke 方法中，通过这个参数可以获取到被调用的方法的信息，如方法名、参数类型等。
    //args 参数表示方法调用时传入的参数数组，即客户端代码调用代理对象方法时传入的参数。在 invoke 方法中，通过这个参数可以获取到方法调用时传入的参数值。
    //这三个参数是由 Java 动态代理机制在调用代理对象的方法时自动传递给 invoke 方法的，开发者无需手动传入。在 invoke 方法中，可以根据这些参数来动态地处理方法调用，例如构造远程调用请求、发送请求、处理响应等。
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("调用动态代理ProxyUser");

        //指定序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        //构造请求
        String serviceName = method.getDeclaringClass().getName();
        //动态代理依赖method里的信息，静态代理直接写死了这里
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            //序列化请求
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            //从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }
            //暂时先去第一个
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
            //发送TCP请求
            RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
            return rpcResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException("调用失败");
        }
//        return null;
    }
}

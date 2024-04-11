package com.wb20.example.provider;

import com.wb20.example.common.service.UserService;
import com.wb20.rrpc.RpcApplication;
import com.wb20.rrpc.config.RegistryConfig;
import com.wb20.rrpc.config.RpcConfig;
import com.wb20.rrpc.model.ServiceMetaInfo;
import com.wb20.rrpc.registry.LocalRegistry;
import com.wb20.rrpc.registry.Registry;
import com.wb20.rrpc.registry.RegistryFactory;
import com.wb20.rrpc.server.HttpServer;
import com.wb20.rrpc.server.VertxHttpServer;

import java.util.HashSet;
import java.util.Set;

/**
 * 服务提供者示例
 */
public class ProviderExample {

    public static void main(String[] args) {
        //RPC 框架初始化
        RpcApplication.init();

        //注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        //注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//        System.out.println("rpcConfig: " + rpcConfig.toString());
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceAddress(rpcConfig.getServerHost() + ":" + rpcConfig.getServerPort());
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
//        System.out.println("serviceMetaInfo: " + serviceMetaInfo.toString());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());


    }

}

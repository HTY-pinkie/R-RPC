package com.wb20.example.provider;

import com.wb20.example.common.service.UserService;
import com.wb20.rrpc.registry.LocalRegistry;
import com.wb20.rrpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        //注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        //启动web服务
        VertxHttpServer httpServer = new VertxHttpServer();
        //调用easy rpc里的方法
        httpServer.doStart(8080);
    }
}

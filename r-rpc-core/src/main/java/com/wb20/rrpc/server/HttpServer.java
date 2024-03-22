package com.wb20.rrpc.server;

/**
 * HTTP 服务器接口
 */
public interface HttpServer {

    /**
     * 启动服务器
     * @param port 需要启动的端口
     */
    void doStart(int port);
}

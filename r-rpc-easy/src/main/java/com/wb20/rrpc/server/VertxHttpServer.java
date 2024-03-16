package com.wb20.rrpc.server;

import io.vertx.core.Vertx;

/**
 * 基于 Vert.x 实现的 web 服务器 VertxHttpServer，能够监听指定端口并处理请求
 */
public class VertxHttpServer implements HttpServer {
    public void doStart(int port) {
        //创建你Vert.x 实例
        Vertx vertx = Vertx.vertx();

        //创建Http服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        //其中request是一个HttpServerRequest对象，表示收到的HTTP请求。在处理器中，通过调用request.method()获取请求的方法（GET、POST等），通过request.uri()获取请求的URI，然后将请求信息打印输出到控制台。
        //监听端口并'处理请求'
        //1.0
//        server.requestHandler(request -> {
//            //处理Http请求
//            System.out.println("Received request: " + request.method() + " " + request.uri());
//
//            //发送Http响应
//            //request.response(): 调用response()方法获取到HTTP请求的响应对象，用于向客户端发送响应。
//            request.response()
//                    //使用putHeader()方法设置响应头部信息，这里设置了content-type为text/plain，表示响应内容的类型为纯文本。
//                    .putHeader("content-type", "text/plain")
//                    //使用end()方法将字符串"Hello from Vert.x HTTP server!"作为响应内容发送给客户端，并结束响应。这意味着响应的内容为"Hello from Vert.x HTTP server!"，并且不会再发送任何其他内容
//                    .end("Hello from Vert.x HTTP server!");
//        });
        //2.0
        server.requestHandler(new HttpServerHandler());

        //启动HTTP服务器并'监听指定端口'
        //port是一个int整型，表示端口号，result是一个AsyncResult<HttpServer>对象，表示启动服务器的异步结果。在Lambda表达式中，首先检查结果是否成功，如果成功，则打印服务器已经开始监听的消息，否则打印启动失败的原因。
        server.listen(port, result -> {
            if(result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.out.println("Failed to start server: " + result.cause());
            }
        });

    }
}

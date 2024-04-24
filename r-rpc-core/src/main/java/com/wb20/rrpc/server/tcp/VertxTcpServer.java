package com.wb20.rrpc.server.tcp;

import com.wb20.rrpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;

public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData) {
        //在这里编写处理请求的逻辑，根据 requestData 构造响应数据并返回
        //这里只是一个示例，实际逻辑需要根据具体的业务需求来实现
        return "Hello, client!".getBytes();
    }

    @Override
    public void doStart(int port) {
        //创建Vert.x 实例
        Vertx vertx = Vertx.vertx();

        //创建 TCP 服务器
        NetServer server = vertx.createNetServer();

        //处理请求
        server.connectHandler(socket -> {
            //处理连接
//            socket.handler(buffer -> {
//                String testMessage = "Hello, server!Hello, server!Hello, server!";
//                int messageLength = testMessage.getBytes().length;
//
//                //构造parser
//                RecordParser parser = RecordParser.newFixed(messageLength);
//                parser.setOutput(new Handler<Buffer>() {
//                    @Override
//                    public void handle(Buffer buffer) {
//                        String str = new String(buffer.getBytes());
//                        System.out.println(str);
//                        if(testMessage.equals(str)) {
//                            System.out.println("good");
//                        } else {
//                            System.out.println("bad man");
//                        }
//                    }
//                });
//                socket.handler(parser);
                //2.0
//                if(buffer.getBytes().length < messageLength) {
//                    System.out.println("半包, length = " + buffer.getBytes().length);
//                }
//                if(buffer.getBytes().length > messageLength) {
//                    System.out.println("粘包, length = " + buffer.getBytes().length);
//                }
//                String str = new String(buffer.getBytes(0, messageLength));
//                System.out.println(str);
//                if(testMessage.equals(str)) {
//                    System.out.println("good");
//                }
                //1.0
//                //处理接收到的字节数组
//                byte[] requestData = buffer.getBytes();
//                //在这里进行自定义的字节数组处理逻辑，比如解析请求，调用服务，构造响应等
//                byte[] responseData = handleRequest(requestData);
//                //发送响应
//                socket.write(Buffer.buffer(responseData));
//                //接收响应
//                System.out.println("Client response from server: " + buffer.toString());
//            });
        });

        server.connectHandler(new TcpServerHandler());

        //启动 TCP 服务器并监听指定端口
        server.listen(port, result -> {
           if(result.succeeded()) {
               System.out.println("TCP server started on port " + port);
           } else {
               System.out.println("Failed to start TCP server: " + result.cause());
           }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}

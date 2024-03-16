package com.wb20.rrpc.server;

import com.wb20.rrpc.model.RpcRequest;
import com.wb20.rrpc.model.RpcResponse;
import com.wb20.rrpc.registry.LocalRegistry;
import com.wb20.rrpc.serializer.JdkSerializer;
import com.wb20.rrpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;


/**
 * HTTP 请求处理
 * 1.反序列化请求为对象，并从请求对象中获取参数。
 * 2.根据服务名称从本地注册器中获取到对应的服务实现类,
 * 3.通过反射机制调用方法，得到返回结果。
 * 4.对返回结果进行封装和序列化，并写入到响应中。
 * 在 Vert.x 中，Handler 接口通常用于处理异步操作的结果或事件，比如处理 HTTP 请求、处理数据库查询结果等。实现 Handler 接口的类可以被注册到 Vert.x 的不同组件中，以处理相应的事件或数据。
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {

        //指定序列化器
        final Serializer serializer = new JdkSerializer();

        //记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());

        //异步处理HTTP请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            //RPC请求对象
            RpcRequest rpcRequest = null;
            try {
                //1.1 反序列化请求为请求对象
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                // Java 中用于打印异常信息的方法调用。当 Java 程序中发生异常时，可以调用这个方法来打印异常信息的堆栈跟踪，以便开发者定位和调试问题
                e.printStackTrace();
            }

            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            // 如果请求为null直接返回
            if(rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                //调用方法：服务器会将响应数据（serializer）发送到客户端，并在发送完成后关闭与客户端的连接
                doResponse(request, rpcResponse, serializer);
                return;
            }

            //获取要调用的服务实现类,通过反射调用
            try {
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                //通过反射从获取到的实现类中，根据方法名和参数类型获取`方法`对象
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                //使用反射调用方法对象，传入实现类的实例以及请求参数，执行远程调用并获取方法执行结果。
                //implClass.newInstance() 是使用反射创建 implClass 类的一个新实例。它调用了 implClass 类的无参构造方法来实例化一个新的对象。这种方式在获取到类对象之后，通过默认的无参构造方法来创建对象实例。
                //method.invoke(implClass.newInstance(), rpcRequest.getArgs())：通过 method 对象的 invoke 方法，调用了 implClass 类的方法。
                // 第一个参数是方法的调用者，也就是方法所属的对象实例，这里是刚刚创建的 implClass 的新实例。
                //第二个参数是方法的参数，通过 rpcRequest.getArgs() 获取。这样就实现了对远程服务方法的调用，并将结果存储在 result 变量中。
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                //封装返回结果
                //设置响应数据
                rpcResponse.setData(result);
                //设置响应数据类型
                rpcResponse.setDataType(method.getReturnType());
                //设置响应信息
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                //在响应中设置错误响应信息
                rpcResponse.setMessage(e.getMessage());
                //设置异常信息
                rpcResponse.setException(e);
            }


        });

    }

    /**
     * 响应
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        //request.response() 返回一个 HttpServerResponse 对象，表示正在处理的 HTTP 请求的响应。然后，putHeader("content-type", "application/json") 方法用于设置响应头，
        //其中 "content-type" 是头字段的名称，"application/json" 是对应的值，表示响应的内容类型为 JSON 格式。
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");

        try {
            // 序列化响应值
            byte[] serialized = serializer.serialize(rpcResponse);
            //httpServerResponse.end(Buffer.buffer(serialized)) 方法将 serialized 数据作为响应的主体，并完成响应的发送。这通常用于在处理完请求后向客户端发送响应数据。
            //在这里，serialized 是一个字节数组或字节缓冲区，通过 Buffer.buffer(serialized) 将其转换为 Vert.x 的缓冲区对象，并将其作为响应主体发送给客户端。
            //一旦调用了 end() 方法，服务器会将响应数据发送到客户端，并在发送完成后关闭与客户端的连接。
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }

    }
}

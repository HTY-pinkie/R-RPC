package com.wb20.rrpc.server.tcp;

import com.wb20.rrpc.RpcApplication;
import com.wb20.rrpc.model.RpcRequest;
import com.wb20.rrpc.model.RpcResponse;
import com.wb20.rrpc.protocol.*;
import com.wb20.rrpc.registry.LocalRegistry;
import com.wb20.rrpc.serializer.Serializer;
import com.wb20.rrpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;


public class TcpServerHandler implements Handler<NetSocket> {
    /**
     * 处理请求
     *
     * @param netSocket the event to handle
     */
    @Override
    public void handle(NetSocket netSocket) {
        //处理连接
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            //接受请求，解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();

            //处理请求
            //构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            try {
                //获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                //封装返回结果
                rpcResponse.setData(result);
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

            //发送响应，编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);

            try {
                Buffer encode = ProtocolMessageEncoder.encode(rpcResponseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }

        });
    }
}

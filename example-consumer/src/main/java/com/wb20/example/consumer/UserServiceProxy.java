package com.wb20.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.wb20.example.common.model.User;
import com.wb20.example.common.service.UserService;
import com.wb20.rrpc.model.RpcRequest;
import com.wb20.rrpc.model.RpcResponse;
import com.wb20.rrpc.serializer.JdkSerializer;
import com.wb20.rrpc.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理
 */
public class UserServiceProxy implements UserService {
    public User getUser(User user) {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();

        //发请求：封装的是User方法
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        //try() {} 是 Java 7 引入的 try-with-resources 语法，用于简化资源管理的代码。在`括号内的代码块中创建的资源对象`会在 try-with-resources 块结束时自动关闭，无需显式调用 close() 方法释放资源。
        //括号内的代码用于创建资源对象。这些资源对象必须实现了 AutoCloseable 接口（或其子接口 Closeable），以便在 try-with-resources 块结束时能够自动关闭。
        //在 try-with-resources 块结束时，Java 会自动调用资源对象的 close() 方法来释放资源，确保资源被正确关闭。
        //这种方式简化了资源管理的代码，并且可以保证资源在使用完毕后及时释放，避免了因忘记手动释放资源而导致的资源泄漏问题。
        try {
            //将请求序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 发送请求
            //创建一个 HTTP POST 请求对象，指定了请求的目标 URL
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    //设置请求体，其中 bodyBytes 是请求体的字节数组，包含了要发送的数据
                    .body(bodyBytes)
                    //执行 HTTP 请求，发送请求到指定的 URL，并返回一个 HttpResponse 对象，表示 HTTP 响应
                    .execute()) {
                //从 HttpResponse 对象中获取响应体的字节数组。
                byte[] result = httpResponse.bodyBytes();
                // 反序列化（字节数组 => Java 对象）
                //使用序列化器 serializer 将响应体的字节数组反序列化成 RpcResponse 对象，即将字节数组转换为 Java 对象。
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                //从 RpcResponse 对象中获取数据，并将其转换为 User 对象返回。这里假设响应中的数据是 User 对象
                return (User) rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

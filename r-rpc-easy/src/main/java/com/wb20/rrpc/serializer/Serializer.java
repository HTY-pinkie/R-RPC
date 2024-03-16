package com.wb20.rrpc.serializer;

import java.io.IOException;

public interface Serializer {

    /**
     * 序列化
     * 这段代码定义了一个泛型方法 serialize，它接受一个类型为 T 的对象作为参数，并返回一个 Byte 数组。在方法声明中，<T> 表示这是一个泛型方法，其中 T 是类型参数，
     * 可以在方法中使用。这意味着 serialize 方法可以接受任何类型的对象，并将其序列化为 Byte 数组。该方法还声明了可能抛出 IOException 异常。
     * @param object
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;


}

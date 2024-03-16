package com.wb20.rrpc.serializer;

import java.io.*;

public class JdkSerializer implements Serializer{

    /**
     * 序列化
     * @param object 需要序列化的对象
     * @return 序列化的字节流
     * @param <T> 对象的类型
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //objectOutputStream可以将序列化之后的数组写道outputStream中
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }

    /**
     * 反序列化
     * @param bytes 传过来字节流
     * @param type 需要反序列化的对象
     * @return 对象
     * @param <T> 序列化的类型
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        //在objectInputStream中进行反序列化
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            //将返回的objectInputStream.readObject()进行格式转换
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            objectInputStream.close();
        }
    }
}

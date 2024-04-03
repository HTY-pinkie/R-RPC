package com.wb20.rrpc.serializer;

import com.wb20.rrpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 序列化器工厂（用于获取序列化器对象）
 *
 */
public class SerializerFactory {

    /**
     * 序列化映射（用于实现单例）
     */
//    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>();
//
//    static {
//        KEY_SERIALIZER_MAP.put(SerializerKeys.JDK, new JdkSerializer());
//        KEY_SERIALIZER_MAP.put(SerializerKeys.JSON, new JsonSerializer());
//        KEY_SERIALIZER_MAP.put(SerializerKeys.KRYO, new KryoSerializer());
//        KEY_SERIALIZER_MAP.put(SerializerKeys.HESSIAN, new HessianSerializer());
//    }
    //可能会导致内存泄漏和性能问题
//    private static final Map KEY_SERIALIZER_MAP = new HashMap<String, Serializer>() {{
//        put(SerializerKeys.JDK, new JdkSerializer());
//        put(SerializerKeys.JSON, new JsonSerializer());
//        put(SerializerKeys.KRYO, new KryoSerializer());
//        put(SerializerKeys.HESSIAN, new HessianSerializer());
//    }};


    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
//    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get("jdk");
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();
    /**
     * 获取实例
     */
    public static Serializer getInstance(String key) {
        //能找到返回key找不到返回default也就是jdk
//        return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);

        return SpiLoader.getInstance(Serializer.class, key);
    }
}

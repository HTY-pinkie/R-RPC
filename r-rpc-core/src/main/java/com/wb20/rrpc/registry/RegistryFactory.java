package com.wb20.rrpc.registry;

import com.wb20.rrpc.spi.SpiLoader;

/**
 * 注册中心工厂（用于获得注册中心对象）
 */
public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取实例
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }

}

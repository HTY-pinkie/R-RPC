package com.wb20.rrpc;

import com.wb20.rrpc.config.RegistryConfig;
import com.wb20.rrpc.config.RpcConfig;
import com.wb20.rrpc.constant.RpcConstant;
import com.wb20.rrpc.registry.Registry;
import com.wb20.rrpc.registry.RegistryFactory;
import com.wb20.rrpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架应用
 * 相当于holder，存放了项目全局用到的变量。双捡锁单例模式实现
 * 支持在获取配置时才调用init()方法实现懒加载
 */
@Slf4j
public class RpcApplication {
    /**
     * volatile 适用于以下场景：
     *
     * 状态标志：用于多线程间的状态标志，如开关变量。
     * 双重检查锁定（Double-Checked Locking）：在单例模式的实现中，使用 volatile 可以确保在多线程环境下正确地初始化单例对象。
     * 实现轻量级的同步机制：比如在某些情况下，使用 volatile 变量代替 synchronized 关键字来实现一些简单的同步操作。
     * 总之，volatile 是一种在多线程环境下确保变量可见性的机制，但并不保证原子性，且适用于一些特定的场景。
     */

    //volatile 是Java中的一个关键字，用于修饰变量。
    //当一个变量被 volatile 修饰时，意味着该变量的值可能会被多个线程同时修改和访问，因此在读取和写入该变量时都会直接从主内存中进行操作，而不会使用线程的本地缓存。
    //volatile 关键字提供了一种轻量级的同步机制，用于确保变量在多线程环境下的可见性，即当一个线程修改了 volatile 变量的值，其他线程能够立即看到这个修改。
    //但是，volatile 不能保证原子性，即复合操作（如递增、递减）不是原子操作，因此在多线程环境下仍然需要额外的同步手段来保证线程安全。
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            //配置加载失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 在Java中，每个类都有一个与之关联的Class对象。使用类级别的锁意味着多个线程在访问 getRpcConfig() 方法时，只有一个线程可以进入同步代码块，其他线程需要等待当前线程执行完毕才能进入。
     * 这种方式确保了在多线程环境下，只有一个线程能够执行 init() 方法进行初始化，避免了多个线程同时执行初始化操作而导致的问题，保证了单例对象的唯一性。
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if(rpcConfig == null) {
            //使用类级别的锁来确保线程安全性
            synchronized (RpcApplication.class) {
                if(rpcConfig == null) {
                    //初始化
                    init();
                }
            }
        }
        return rpcConfig;
    }

}

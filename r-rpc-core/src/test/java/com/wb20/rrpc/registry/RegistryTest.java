package com.wb20.rrpc.registry;

import com.wb20.rrpc.config.RegistryConfig;
import com.wb20.rrpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * 注册中心测试
 */
public class RegistryTest {

    final Registry registry = new EtcdRegistry();

    //标识一个方法在每个测试方法执行之前都要运行。通常，这种方法用于执行一些准备工作，例如初始化对象或设置测试环境。
    @Before
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }

    @Test
    public void register() throws Exception {
        //1.1设置服务元信息（注册信息）
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        //1.2注册服务
        registry.register(serviceMetaInfo);

        //2.1设置服务元信息（注册信息）
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1235);
        //2.2注册服务
        registry.register(serviceMetaInfo);

        //3.1设置服务元信息（注册信息）
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("2.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        //3.2注册服务
        registry.register(serviceMetaInfo);

    }

    //todo：删除键值对失败
    @Test
    public void unRegister() {
        //1.设置服务元信息（注册信息）
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        //2.注销服务（服务端）
        registry.unRegister(serviceMetaInfo);
    }

    @Test
    public void serviceDiscover() {
        //1.设置服务元信息（注册信息）
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        //2.获取服务键名
        String serviceKey = serviceMetaInfo.getServiceKey();
        //3.服务发现（获取某服务的所有节点，消费端）
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
        //断言确保了对象 serviceMetaInfoList 不为空。如果 serviceMetaInfoList 为空，则测试将失败
        Assert.assertNotNull(serviceMetaInfoList);
    }
}

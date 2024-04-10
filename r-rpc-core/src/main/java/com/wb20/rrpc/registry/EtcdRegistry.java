package com.wb20.rrpc.registry;

import cn.hutool.json.JSONUtil;
import com.wb20.rrpc.config.RegistryConfig;
import com.wb20.rrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry{

    private Client client;

    private KV kvClient;

    /**
     * 根节点 Nodes创建的文件夹就是从这里来
     * 关于文件夹的提取应该跟字节序列化（ByteSequence）有关
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 初始化注册中心客户端
     * @param registryConfig RPC 框架注册中心配置
     */
    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().
                    //注册中心地址
                    endpoints(registryConfig.getAddress()).
                    //设置超时时间
                    connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).
                    build();
        kvClient = client.getKVClient();
    }

    /**
     * 注册服务
     * @param serviceMetaInfo 服务元信息（注册信息）
     * @throws Exception
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //创建 Lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        //创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        //设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        //ByteSequence 是 etcd Java 客户端库中的一个类，用于表示字节序列
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        System.out.println("register:" + key);

        //将键值对与租约关联起来，并设置过期时间,PutOption 是 etcd Java 客户端库中的一个类，用于表示在使用 Put 操作时的选项
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        System.out.println(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey());

    }

    /**
     * 注销服务（服务端）
     * @param serviceMetaInfo 服务元信息（注册信息）
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {

        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH +
                serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));
        System.out.println(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey());
    }

    /**
     * 服务发现（获取某服务的所有节点，消费端）
     * @param serviceKey 服务健名
     * @return
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {

        //前缀搜索， 结尾一定要加'/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + '/';

        try {
            //前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            System.out.println("前缀查询：" + searchPrefix);
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            System.out.println("EtcdRegistry中:" + keyValues);
            //解析服务信息
            return keyValues.stream()
                    .map(keyValue -> {
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    /**
     * 服务销毁
     */
    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        //释放资源
        if(kvClient != null) {
            kvClient.close();
        }
        if(client != null) {
            client.close();
        }
    }
}

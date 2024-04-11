package com.wb20.rrpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.wb20.rrpc.config.RegistryConfig;
import com.wb20.rrpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry{

    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 本机注册的节点key集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

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
        heartBeat();
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

//        System.out.println("register:" + key);

        //将键值对与租约关联起来，并设置过期时间,PutOption 是 etcd Java 客户端库中的一个类，用于表示在使用 Put 操作时的选项
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
//        System.out.println(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey());

        //添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registerKey);

    }

    /**
     * 注销服务（服务端）
     * @param serviceMetaInfo 服务元信息（注册信息）
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {

        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();

        try {
            //todo 不加get删不了
            kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8)).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey());
        //从本地缓存移除
        localRegisterNodeKeySet.remove(registerKey);
    }

    /**
     * 服务发现（获取某服务的所有节点，消费端）
     * @param serviceKey 服务健名
     * @return
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {

        //优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if(cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }


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

        //下线节点
        //遍历本节点所有的key
        for(String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }

        //释放资源
        if(kvClient != null) {
            kvClient.close();
        }
        if(client != null) {
            client.close();
        }
    }

    /**
     * 心跳检测（服务端）
     */
    @Override
    public void heartBeat() {

        //10s续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                //遍历本届点所有的key
                for(String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
                        //该节点已过期（需要重启节点才能重新注册）
                        if(CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        //节点未过期，重新注册（相当于续签）
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }

            }
        } );

        //支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();

    }
}

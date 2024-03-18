package com.wb20.example.consumer;

import com.wb20.rrpc.config.RpcConfig;
import com.wb20.rrpc.utils.ConfigUtils;

public class ConsumerExample {

    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
    }

}

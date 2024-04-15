package com.wb20.example.consumer;

import com.wb20.example.common.model.User;
import com.wb20.example.common.service.UserService;
import com.wb20.rrpc.proxy.ServiceProxyFactory;

public class ConsumerExample {

    public static void main(String[] args) {
        //获取代理实例
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("blink");
        //调用
//        for(int i = 0; i < 3; i++) {
//            User newUser = userService.getUser(user);
//            if(newUser != null) {
//                System.out.println(newUser.getName());
//            } else {
//                System.out.println("user == null");
//            }
//        }
        User newUser = userService.getUser(user);
        if(newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
//        int number = userService.getNumber();
//        System.out.println(number);
    }

}

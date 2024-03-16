package com.wb20.example.consumer;

import com.wb20.example.common.model.User;
import com.wb20.example.common.service.UserService;

/**
 * 建议服务消费者示例
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        //todo 需要获取 UserService的实现类对象
        UserService userService = null;
        User user = new User();
        user.setName("wb20");
        //调用
        User newUser = userService.getUser(user);
        if(newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}

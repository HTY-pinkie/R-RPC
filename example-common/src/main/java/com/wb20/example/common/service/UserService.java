package com.wb20.example.common.service;

import com.wb20.example.common.model.User;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 获取用户
     * @param user
     * @return user
     */
    User getUser(User user);
}

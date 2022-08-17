package com.example.demo.service;

import com.example.demo.pojo.User;

/**
 * @author czh
 */
public interface UserService {
    int isUser(String openid);

    int insertUser(User user);

    User getUser(String openid);
}
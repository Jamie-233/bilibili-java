package com.bilibili.dao;

import com.bilibili.domain.User;
import com.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    User getUserByPhone(String phone);

    Long addUser(User user);

    Long addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoByUserId(Long userId);
}

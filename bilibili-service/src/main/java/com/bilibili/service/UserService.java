package com.bilibili.service;

import com.bilibili.dao.UserDao;
import com.bilibili.domain.User;
import com.bilibili.domain.UserInfo;
import com.bilibili.domain.constant.UserConstant;
import com.bilibili.domain.exception.ConditionException;
import com.bilibili.service.util.MD5Util;
import com.bilibili.service.util.RSAUtil;
import com.bilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void addUser (User user) {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("phone number cannot be empty");
        }

        User dbUser = this.getUserByPhone(phone);
        if(dbUser != null) {
            throw new ConditionException("that phone number already registered");
        }

        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();

        String rawPassword;

        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception ex) {
            throw new ConditionException("password decryption failed");
        }

        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setCreateTime(now);
        user.setPassword(md5Password);

        Long id = userDao.addUser(user);
        user.setId(id);

        UserInfo userInfo = new UserInfo();

        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setCreateTime(now);

        userDao.addUserInfo(userInfo);
    }

    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }

    public String login(User user) throws Exception {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("phone number cannot be empty");
        }

        User dbUser = this.getUserByPhone(phone);
        if(dbUser == null) {
            throw new ConditionException("user does not exist");
        }

        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception ex) {
            throw new ConditionException("password decryption failed");
        }

        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if(!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("password error");
        }

        return TokenUtil.generateToken(dbUser.getId());
    }

    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);

        return user;
    }

    public UserInfo updateUserInfos(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfos(userInfo);

        return userInfo;
    }

    public User getUserById(Long followingId) {
        return userDao.getUserById(followingId);
    }

    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.getUserInfoByUserIds(userIdList);
    }
}

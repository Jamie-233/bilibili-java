package com.bilibili.service;

import com.bilibili.dao.FollowingGroupDao;
import com.bilibili.domain.FollowingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowingGroupService {

    @Autowired
    private FollowingGroupDao userFollowingDao;

    public FollowingGroup getByType(String type) {
        return userFollowingDao.getByType(type);
    }

    public FollowingGroup getById(Long id) {
        return userFollowingDao.getById(id);
    }

    public List<FollowingGroup> getByUserId(Long userId) {
        return userFollowingDao.getByUserId(userId);
    }
}

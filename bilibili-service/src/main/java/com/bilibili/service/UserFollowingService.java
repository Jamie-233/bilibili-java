package com.bilibili.service;

import com.bilibili.dao.FollowingGroupDao;
import com.bilibili.dao.UserFollowingDao;
import com.bilibili.domain.FollowingGroup;
import com.bilibili.domain.User;
import com.bilibili.domain.UserFollowing;
import com.bilibili.domain.UserInfo;
import com.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bilibili.domain.constant.UserConstant.USER_FOLLOWING_ALL_NAME;
import static com.bilibili.domain.constant.UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT;

@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupDao followingGroupDao;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Transactional
    public void addUserFollowings(UserFollowing userFollowing) {
        Long groupId = userFollowing.getGroupId();
        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);

        if (groupId == null) {
            FollowingGroup followingGroup = followingGroupDao.getByType(USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            FollowingGroup followingGroup = followingGroupDao.getById(groupId);

            if(followingGroup == null) {
                throw new ConditionException("Group does not exist");
            }
        }

        if (user == null) {
            throw new ConditionException("User does not exist");
        }

        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }

    // 获取用户关注列表
    // 根据关注用户的ID查询关注用户的基本信息
    // 将关注用户按照分组进行分类
    public List<FollowingGroup> getUserFollowings(Long userId) {
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();

        if(followingIdSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }

        for (UserFollowing userFollowing : list) {
            for (UserInfo userInfo : userInfoList) {
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }

        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(USER_FOLLOWING_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);

        for (FollowingGroup group : groupList) {
            List<UserInfo> infoList = new ArrayList<>();
            for (UserFollowing userFollowing : list) {
                if(group.getId().equals(userFollowing.getFollowingId())) {
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }

        return null;
    }

    // 获取当前用户的粉丝列表
    // 根据粉丝的用户ID查询基本信息
    // 查询当前用户是否已经关注该粉丝
    public List<UserFollowing> getUserFans(Long userId) {
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if(fanList.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        for(UserFollowing fan : fanList) {
            for(UserInfo userInfo : userInfoList) {
                if(fan.getUserId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }

            for(UserFollowing following : followingList) {
                if(following.getFollowingId().equals(fan.getId())) {
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }

        return fanList;
    }
}

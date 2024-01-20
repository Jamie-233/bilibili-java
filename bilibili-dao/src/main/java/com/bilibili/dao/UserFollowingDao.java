package com.bilibili.dao;

import com.bilibili.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFollowingDao {
    void deleteUserFollowing(@Param("userId") Long userId, @Param("followingId")Long followingId);

    void addUserFollowing(UserFollowing userFollowing);

    List<UserFollowing> getUserFollowings(Long userId);

    List<UserFollowing> getUserFans(Long userId);
}

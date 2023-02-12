package com.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface DemoDao {
    public Map<String, Object> query(Long id);
}

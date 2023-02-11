package com.bilibili.service;

import com.bilibili.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;

    public Long query(Long id) {
        return demoDao.query(id);
    }
}

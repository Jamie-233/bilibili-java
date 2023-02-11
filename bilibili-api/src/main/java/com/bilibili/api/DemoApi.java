package com.bilibili.api;

import com.bilibili.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoApi {

    @Autowired
    private DemoService demoService;

    @GetMapping("/query")
    public Long query(Long id) {
        return demoService.query(id);
    }
}

package com.example.demo.config;

import com.example.demo.entity.DemoEntity;
import com.example.demo.repository.DemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DemoRepository demoRepository;

    @Override
    public void run(String... args) throws Exception {
        // 初始化一些测试数据
        if (demoRepository.count() == 0) {
            demoRepository.save(new DemoEntity("测试数据1", "TEST001"));
            demoRepository.save(new DemoEntity("测试数据2", "TEST002"));
            demoRepository.save(new DemoEntity("演示数据", "DEMO001"));
            System.out.println("初始化测试数据完成");
        }
    }
}
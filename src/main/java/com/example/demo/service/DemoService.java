package com.example.demo.service;

import com.example.demo.entity.DemoEntity;
import com.example.demo.repository.DemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DemoService {

    @Autowired
    private DemoRepository demoRepository;

    // 获取所有数据
    public List<DemoEntity> getAll() {
        return demoRepository.findAll();
    }

    // 根据ID获取数据
    public Optional<DemoEntity> getById(Long id) {
        return demoRepository.findById(id);
    }

    // 创建新数据
    public DemoEntity create(DemoEntity entity) {
        // 检查代码是否已存在
        if (demoRepository.existsByCode(entity.getCode())) {
            throw new RuntimeException("代码已存在: " + entity.getCode());
        }
        return demoRepository.save(entity);
    }

    // 更新数据
    public DemoEntity update(Long id, DemoEntity entity) {
        Optional<DemoEntity> existing = demoRepository.findById(id);
        if (existing.isPresent()) {
            DemoEntity demoEntity = existing.get();
            
            // 如果代码被修改，检查新代码是否已存在
            if (!demoEntity.getCode().equals(entity.getCode()) && 
                demoRepository.existsByCode(entity.getCode())) {
                throw new RuntimeException("代码已存在: " + entity.getCode());
            }
            
            demoEntity.setName(entity.getName());
            demoEntity.setCode(entity.getCode());
            return demoRepository.save(demoEntity);
        }
        return null;
    }

    // 删除数据
    public boolean delete(Long id) {
        if (demoRepository.existsById(id)) {
            demoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 根据代码查询
    public Optional<DemoEntity> getByCode(String code) {
        return demoRepository.findByCode(code);
    }

    // 根据名称模糊查询
    public List<DemoEntity> searchByName(String name) {
        return demoRepository.findByNameContaining(name);
    }
}
/*
package com.example.demo.repository;

import com.example.demo.entity.DemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemoRepository extends JpaRepository<DemoEntity, Long> {
    
    // 根据名称查询
    List<DemoEntity> findByName(String name);
    
    // 根据名称模糊查询
    List<DemoEntity> findByNameContaining(String name);
    
    // 根据代码查询
    Optional<DemoEntity> findByCode(String code);
    
    // 自定义查询：根据名称和代码查询
    @Query("SELECT d FROM DemoEntity d WHERE d.name = :name AND d.code = :code")
    List<DemoEntity> findByNameAndCode(@Param("name") String name, @Param("code") String code);
    
    // 检查代码是否存在
    boolean existsByCode(String code);
}*/

package com.example.demo.controller;

import com.example.demo.entity.DemoEntity;
import com.example.demo.service.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/demo")
@Api(tags = "Demo CRUD接口")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping
    @ApiOperation("获取所有数据")
    public List<DemoEntity> getAll() {
        return demoService.getAll();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取数据")
    public ResponseEntity<DemoEntity> getById(@ApiParam("数据ID") @PathVariable Long id) {
        Optional<DemoEntity> entity = demoService.getById(id);
        return entity.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @ApiOperation("根据代码获取数据")
    public ResponseEntity<DemoEntity> getByCode(@ApiParam("数据代码") @PathVariable String code) {
        Optional<DemoEntity> entity = demoService.getByCode(code);
        return entity.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @ApiOperation("根据名称搜索数据")
    public List<DemoEntity> searchByName(@ApiParam("名称关键词") @RequestParam String name) {
        return demoService.searchByName(name);
    }

    @PostMapping
    @ApiOperation("创建新数据")
    public ResponseEntity<?> create(@RequestBody DemoEntity entity) {
        try {
            DemoEntity saved = demoService.create(entity);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("更新数据")
    public ResponseEntity<?> update(@ApiParam("数据ID") @PathVariable Long id, @RequestBody DemoEntity entity) {
        try {
            DemoEntity updated = demoService.update(id, entity);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除数据")
    public ResponseEntity<String> delete(@ApiParam("数据ID") @PathVariable Long id) {
        boolean deleted = demoService.delete(id);
        return deleted ? ResponseEntity.ok("删除成功") : ResponseEntity.notFound().build();
    }
}
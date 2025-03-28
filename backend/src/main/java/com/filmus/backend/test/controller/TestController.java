package com.filmus.backend.test.controller;

import com.filmus.backend.test.entity.TestEntity;
import com.filmus.backend.test.repository.TestRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    private final TestRepository testRepository;

    public TestController(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    // 🔍 전체 조회
    @GetMapping("/all")
    public List<TestEntity> getAll() {
        return testRepository.findAll();
    }

    // ➕ 값 추가
    @PostMapping("/add")
    public TestEntity add(@RequestBody TestEntity test) {
        return testRepository.save(test);
    }

    // 📘 단일 조회 (id)
    @GetMapping("/{id}")
    public TestEntity getById(@PathVariable Long id) {
        return testRepository.findById(id).orElse(null);
    }
}
package com.example.demo.controller;

import com.example.demo.dto.CreateItemRequest;
import com.example.demo.dto.ItemResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.UpdateItemRequest;
import com.example.demo.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@Tag(name = "Items", description = "CRUD APIs for demo items")
public class ItemController {

    private final ItemService itemService;

    ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    @Operation(summary = "List items")
    PageResponse<ItemResponse> list(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return itemService.list(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by id")
    ItemResponse get(@PathVariable Long id) {
        return itemService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an item")
    ItemResponse create(@Valid @RequestBody CreateItemRequest request) {
        return itemService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an item")
    ItemResponse update(@PathVariable Long id, @Valid @RequestBody UpdateItemRequest request) {
        return itemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an item")
    void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}

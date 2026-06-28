package com.example.demo.service;

import com.example.demo.dto.CreateItemRequest;
import com.example.demo.dto.ItemResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.UpdateItemRequest;
import com.example.demo.entity.Item;
import com.example.demo.repository.ItemRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemResponse> list(Pageable pageable) {
        return PageResponse.from(itemRepository.findAll(pageable).map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public ItemResponse get(Long id) {
        return toResponse(findItem(id));
    }

    @Transactional
    public ItemResponse create(CreateItemRequest request) {
        Item item = new Item(request.name(), request.description());
        return toResponse(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse update(Long id, UpdateItemRequest request) {
        Item item = findItem(id);
        item.rename(request.name());
        item.describe(request.description());
        return toResponse(itemRepository.saveAndFlush(item));
    }

    @Transactional
    public void delete(Long id) {
        Item item = findItem(id);
        itemRepository.delete(item);
    }

    private Item findItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found: " + id));
    }

    private ItemResponse toResponse(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getDescription(),
                item.getCreatedAt(), item.getUpdatedAt());
    }
}

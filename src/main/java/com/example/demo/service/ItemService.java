package com.example.demo.service;

import com.example.demo.dto.CreateItemRequest;
import com.example.demo.dto.ItemResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.UpdateItemRequest;
import com.example.demo.entity.Item;
import com.example.demo.repository.ItemRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;
    private final Tracer tracer;

    public ItemService(ItemRepository itemRepository, Tracer tracer) {
        this.itemRepository = itemRepository;
        this.tracer = tracer;
    }

    @Transactional(readOnly = true)
    public PageResponse<ItemResponse> list(Pageable pageable) {
        PageResponse<ItemResponse> response = PageResponse.from(itemRepository.findAll(pageable).map(this::toResponse));
        log.info("ItemService.list page={} size={} totalElements={} traceId={} spanId={}",
                response.page(), response.size(), response.totalElements(), traceId(), spanId());
        return response;
    }

    @Transactional(readOnly = true)
    public ItemResponse get(Long id) {
        ItemResponse response = toResponse(findItem(id));
        log.info("ItemService.get id={} traceId={} spanId={}", id, traceId(), spanId());
        return response;
    }

    @Transactional
    public ItemResponse create(CreateItemRequest request) {
        Item item = new Item(request.name(), request.description());
        ItemResponse response = toResponse(itemRepository.save(item));
        log.info("ItemService.create id={} name={} traceId={} spanId={}",
                response.id(), response.name(), traceId(), spanId());
        return response;
    }

    @Transactional
    public ItemResponse update(Long id, UpdateItemRequest request) {
        Item item = findItem(id);
        item.rename(request.name());
        item.describe(request.description());
        ItemResponse response = toResponse(itemRepository.saveAndFlush(item));
        log.info("ItemService.update id={} name={} traceId={} spanId={}",
                response.id(), response.name(), traceId(), spanId());
        return response;
    }

    @Transactional
    public void delete(Long id) {
        Item item = findItem(id);
        itemRepository.delete(item);
        log.info("ItemService.delete id={} traceId={} spanId={}", id, traceId(), spanId());
    }

    private Item findItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found: " + id));
    }

    private ItemResponse toResponse(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getDescription(),
                item.getCreatedAt(), item.getUpdatedAt());
    }

    private String traceId() {
        Span span = tracer.currentSpan();
        return span == null ? "none" : span.context().traceId();
    }

    private String spanId() {
        Span span = tracer.currentSpan();
        return span == null ? "none" : span.context().spanId();
    }
}

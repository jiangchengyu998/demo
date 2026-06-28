package com.example.demo.service;

import com.example.demo.dto.CreateItemRequest;
import com.example.demo.dto.ItemResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.dto.UpdateItemRequest;
import com.example.demo.entity.Item;
import com.example.demo.repository.ItemRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

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
        return trace("ItemService.list", () -> {
            Page<Item> items = trace("db.items.findAll", () -> itemRepository.findAll(pageable),
                    "page", String.valueOf(pageable.getPageNumber()),
                    "size", String.valueOf(pageable.getPageSize()));
            PageResponse<ItemResponse> response = PageResponse.from(items.map(this::toResponse));
            log.info("ItemService.list page={} size={} totalElements={} traceId={} spanId={}",
                    response.page(), response.size(), response.totalElements(), traceId(), spanId());
            return response;
        }, "page", String.valueOf(pageable.getPageNumber()), "size", String.valueOf(pageable.getPageSize()));
    }

    @Transactional(readOnly = true)
    public ItemResponse get(Long id) {
        return trace("ItemService.get", () -> {
            ItemResponse response = toResponse(findItem(id));
            log.info("ItemService.get id={} traceId={} spanId={}", id, traceId(), spanId());
            return response;
        }, "item.id", String.valueOf(id));
    }

    @Transactional
    public ItemResponse create(CreateItemRequest request) {
        return trace("ItemService.create", () -> {
            Item item = new Item(request.name(), request.description());
            Item saved = trace("db.items.save", () -> itemRepository.save(item), "item.name", request.name());
            ItemResponse response = toResponse(saved);
            log.info("ItemService.create id={} name={} traceId={} spanId={}",
                    response.id(), response.name(), traceId(), spanId());
            return response;
        }, "item.name", request.name());
    }

    @Transactional
    public ItemResponse update(Long id, UpdateItemRequest request) {
        return trace("ItemService.update", () -> {
            Item item = findItem(id);
            item.rename(request.name());
            item.describe(request.description());
            Item saved = trace("db.items.saveAndFlush", () -> itemRepository.saveAndFlush(item),
                    "item.id", String.valueOf(id),
                    "item.name", request.name());
            ItemResponse response = toResponse(saved);
            log.info("ItemService.update id={} name={} traceId={} spanId={}",
                    response.id(), response.name(), traceId(), spanId());
            return response;
        }, "item.id", String.valueOf(id), "item.name", request.name());
    }

    @Transactional
    public void delete(Long id) {
        trace("ItemService.delete", () -> {
            Item item = findItem(id);
            trace("db.items.delete", () -> itemRepository.delete(item), "item.id", String.valueOf(id));
            log.info("ItemService.delete id={} traceId={} spanId={}", id, traceId(), spanId());
        }, "item.id", String.valueOf(id));
    }

    private Item findItem(Long id) {
        return trace("db.items.findById", () -> itemRepository.findById(id), "item.id", String.valueOf(id))
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

    private <T> T trace(String spanName, Supplier<T> operation, String... tags) {
        Span span = tracer.nextSpan().name(spanName);
        tag(span, tags);
        long startedAt = System.nanoTime();
        try (Tracer.SpanInScope ignored = tracer.withSpan(span.start())) {
            T result = operation.get();
            long durationMs = durationMs(startedAt);
            span.tag("duration.ms", String.valueOf(durationMs));
            log.info("{} completed durationMs={} traceId={} spanId={}", spanName, durationMs, traceId(), spanId());
            return result;
        } catch (RuntimeException ex) {
            long durationMs = durationMs(startedAt);
            span.tag("duration.ms", String.valueOf(durationMs));
            span.error(ex);
            log.warn("{} failed durationMs={} traceId={} spanId={} error={}",
                    spanName, durationMs, traceId(), spanId(), ex.getClass().getSimpleName());
            throw ex;
        } finally {
            span.end();
        }
    }

    private void trace(String spanName, Runnable operation, String... tags) {
        trace(spanName, () -> {
            operation.run();
            return null;
        }, tags);
    }

    private static void tag(Span span, String... tags) {
        for (int i = 0; i + 1 < tags.length; i += 2) {
            span.tag(tags[i], tags[i + 1]);
        }
    }

    private static long durationMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}

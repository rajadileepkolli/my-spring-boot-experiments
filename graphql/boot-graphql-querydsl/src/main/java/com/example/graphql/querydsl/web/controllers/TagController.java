package com.example.graphql.querydsl.web.controllers;

import com.example.graphql.querydsl.exception.TagNotFoundException;
import com.example.graphql.querydsl.model.query.FindQuery;
import com.example.graphql.querydsl.model.request.TagRequest;
import com.example.graphql.querydsl.model.response.PagedResult;
import com.example.graphql.querydsl.model.response.TagResponse;
import com.example.graphql.querydsl.services.TagService;
import com.example.graphql.querydsl.utils.AppConstants;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/tags")
class TagController {

    private final TagService tagService;

    TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    PagedResult<TagResponse> getAllTags(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        FindQuery findTagsQuery = new FindQuery(pageNo, pageSize, sortBy, sortDir);
        return tagService.findAllTags(findTagsQuery);
    }

    @GetMapping("/{id}")
    ResponseEntity<TagResponse> getTagById(@PathVariable Long id) {
        return tagService.findTagById(id).map(ResponseEntity::ok).orElseThrow(() -> new TagNotFoundException(id));
    }

    @PostMapping
    ResponseEntity<TagResponse> createTag(@RequestBody @Validated TagRequest tagRequest) {
        TagResponse response = tagService.saveTag(tagRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/api/tags/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<TagResponse> updateTag(@PathVariable Long id, @RequestBody @Valid TagRequest tagRequest) {
        return ResponseEntity.ok(tagService.updateTag(id, tagRequest));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<TagResponse> deleteTag(@PathVariable Long id) {
        return tagService
                .findTagById(id)
                .map(tag -> {
                    tagService.deleteTagById(id);
                    return ResponseEntity.ok(tag);
                })
                .orElseThrow(() -> new TagNotFoundException(id));
    }
}

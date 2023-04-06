package com.example.graphql.web.controllers;

import static com.example.graphql.utils.AppConstants.PROFILE_TEST;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.graphql.entities.TagEntity;
import com.example.graphql.services.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = TagController.class)
@ActiveProfiles(PROFILE_TEST)
class TagEntityControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private TagService tagService;

    @Autowired private ObjectMapper objectMapper;

    private List<TagEntity> tagEntityList;

    @BeforeEach
    void setUp() {
        this.tagEntityList = new ArrayList<>();
        this.tagEntityList.add(new TagEntity(1L, "text 1", null));
        this.tagEntityList.add(new TagEntity(2L, "text 2", null));
        this.tagEntityList.add(new TagEntity(3L, "text 3", null));
    }

    @Test
    void shouldFetchAllTags() throws Exception {
        given(tagService.findAllTags()).willReturn(this.tagEntityList);

        this.mockMvc
                .perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(tagEntityList.size())));
    }

    @Test
    void shouldFindTagById() throws Exception {
        Long tagId = 1L;
        TagEntity tagEntity = new TagEntity(tagId, "text 1", null);
        given(tagService.findTagById(tagId)).willReturn(Optional.of(tagEntity));

        this.mockMvc
                .perform(get("/api/tags/{id}", tagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagName", is(tagEntity.getTagName())));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistingTag() throws Exception {
        Long tagId = 1L;
        given(tagService.findTagById(tagId)).willReturn(Optional.empty());

        this.mockMvc.perform(get("/api/tags/{id}", tagId)).andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewTag() throws Exception {
        given(tagService.saveTag(any(TagEntity.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        TagEntity tagEntity = new TagEntity(1L, "some text", null);
        this.mockMvc
                .perform(
                        post("/api/tags")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tagEntity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.tagName", is(tagEntity.getTagName())));
    }

    @Test
    void shouldUpdateTag() throws Exception {
        Long tagId = 1L;
        TagEntity tagEntity = new TagEntity(tagId, "Updated text", null);
        given(tagService.findTagById(tagId)).willReturn(Optional.of(tagEntity));
        given(tagService.saveTag(any(TagEntity.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc
                .perform(
                        put("/api/tags/{id}", tagEntity.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tagEntity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagName", is(tagEntity.getTagName())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingTag() throws Exception {
        Long tagId = 1L;
        given(tagService.findTagById(tagId)).willReturn(Optional.empty());
        TagEntity tagEntity = new TagEntity(tagId, "Updated text", null);

        this.mockMvc
                .perform(
                        put("/api/tags/{id}", tagId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tagEntity)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteTag() throws Exception {
        Long tagId = 1L;
        TagEntity tagEntity = new TagEntity(tagId, "Some text", null);
        given(tagService.findTagById(tagId)).willReturn(Optional.of(tagEntity));
        doNothing().when(tagService).deleteTagById(tagEntity.getId());

        this.mockMvc
                .perform(delete("/api/tags/{id}", tagEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagName", is(tagEntity.getTagName())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingTag() throws Exception {
        Long tagId = 1L;
        given(tagService.findTagById(tagId)).willReturn(Optional.empty());

        this.mockMvc.perform(delete("/api/tags/{id}", tagId)).andExpect(status().isNotFound());
    }
}

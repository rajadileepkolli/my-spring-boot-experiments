package com.example.keysetpagination.web.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.keysetpagination.common.AbstractIntegrationTest;
import com.example.keysetpagination.entities.Actor;
import com.example.keysetpagination.model.request.ActorRequest;
import com.example.keysetpagination.model.response.ActorResponse;
import com.example.keysetpagination.model.response.PagedResult;
import com.example.keysetpagination.repositories.ActorRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class ActorControllerIT extends AbstractIntegrationTest {

    @Autowired private ActorRepository actorRepository;

    private List<Actor> actorList = null;

    @BeforeEach
    void setUp() {
        actorRepository.deleteAllInBatch();

        actorList = new ArrayList<>();
        actorList.add(new Actor(null, "First Actor", LocalDate.now()));
        actorList.add(new Actor(null, "Second Actor", LocalDate.now()));
        actorList.add(new Actor(null, "Third Actor", LocalDate.now()));
        actorList = actorRepository.saveAll(actorList);
    }

    @Test
    void shouldFetchAllActors() throws Exception {
        String contentAsString =
                this.mockMvc
                        .perform(get("/api/actors?pageNo=0&pageSize=2&sortDir=desc"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.size()", is(2)))
                        .andExpect(jsonPath("$.totalElements", is(3)))
                        .andExpect(jsonPath("$.pageNumber", is(1)))
                        .andExpect(jsonPath("$.totalPages", is(2)))
                        .andExpect(jsonPath("$.isFirst", is(true)))
                        .andExpect(jsonPath("$.isLast", is(false)))
                        .andExpect(jsonPath("$.hasNext", is(true)))
                        .andExpect(jsonPath("$.hasPrevious", is(false)))
                        .andExpect(jsonPath("$.keySetPageResponse.maxResults", is(2)))
                        .andExpect(jsonPath("$.keySetPageResponse.firstResult", is(0)))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        PagedResult<ActorResponse> pagedResult =
                objectMapper.readValue(contentAsString, PagedResult.class);

        this.mockMvc
                .perform(
                        get("/api/actors")
                                .param("pageNo", "2")
                                .param("pageSize", "2")
                                .param("sortDir", "desc")
                                .param("prevPage", String.valueOf(pagedResult.pageNumber()))
                                .param("maxResults", "2")
                                .param(
                                        "lowest",
                                        String.valueOf(pagedResult.keySetPageResponse().lowest()))
                                .param(
                                        "highest",
                                        String.valueOf(pagedResult.keySetPageResponse().highest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()", is(1)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.pageNumber", is(2)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.isFirst", is(false)))
                .andExpect(jsonPath("$.isLast", is(true)))
                .andExpect(jsonPath("$.hasNext", is(false)))
                .andExpect(jsonPath("$.hasPrevious", is(true)))
                .andExpect(jsonPath("$.keySetPageResponse.maxResults", is(2)))
                .andExpect(jsonPath("$.keySetPageResponse.firstResult", is(2)));
    }

    @Test
    void shouldFindActorById() throws Exception {
        Actor actor = actorList.get(0);
        Long actorId = actor.getId();

        this.mockMvc
                .perform(get("/api/actors/{id}", actorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(actor.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(actor.getText())));
    }

    @Test
    void shouldCreateNewActor() throws Exception {
        ActorRequest actorRequest = new ActorRequest("New Actor");
        this.mockMvc
                .perform(
                        post("/api/actors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(actorRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.text", is(actorRequest.text())));
    }

    @Test
    void shouldReturn400WhenCreateNewActorWithoutText() throws Exception {
        ActorRequest actorRequest = new ActorRequest(null);

        this.mockMvc
                .perform(
                        post("/api/actors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(actorRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andExpect(jsonPath("$.instance", is("/api/actors")))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("text")))
                .andExpect(jsonPath("$.violations[0].message", is("Text cannot be empty")))
                .andReturn();
    }

    @Test
    void shouldUpdateActor() throws Exception {
        Long actorId = actorList.get(0).getId();
        ActorRequest actorRequest = new ActorRequest("Updated Actor");

        this.mockMvc
                .perform(
                        put("/api/actors/{id}", actorId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(actorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(actorId), Long.class))
                .andExpect(jsonPath("$.text", is(actorRequest.text())));
    }

    @Test
    void shouldDeleteActor() throws Exception {
        Actor actor = actorList.get(0);

        this.mockMvc
                .perform(delete("/api/actors/{id}", actor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(actor.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(actor.getText())));
    }
}

package com.example.graphql.web.controllers;

import com.example.graphql.model.request.AuthorRequest;
import com.example.graphql.model.response.AuthorResponse;
import com.example.graphql.services.AuthorService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public List<AuthorResponse> getAllAuthors() {
        return authorService.findAllAuthors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        return authorService
                .findAuthorById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse createAuthor(@RequestBody @Validated AuthorRequest authorRequest) {
        return authorService.saveAuthor(authorRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable Long id, @RequestBody AuthorRequest authorRequest) {
        return authorService
                .updateAuthor(authorRequest, id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AuthorResponse> deleteAuthor(@PathVariable Long id) {
        return authorService
                .findAuthorById(id)
                .map(
                        author -> {
                            authorService.deleteAuthorById(id);
                            return ResponseEntity.ok(author);
                        })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

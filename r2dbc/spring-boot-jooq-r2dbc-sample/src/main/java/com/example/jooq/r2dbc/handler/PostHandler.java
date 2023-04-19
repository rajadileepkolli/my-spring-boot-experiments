package com.example.jooq.r2dbc.handler;

import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.example.jooq.r2dbc.config.logging.Loggable;
import com.example.jooq.r2dbc.entities.Post;
import com.example.jooq.r2dbc.model.request.CreatePostCommand;
import com.example.jooq.r2dbc.model.request.CreatePostComment;
import com.example.jooq.r2dbc.model.response.PaginatedResult;
import com.example.jooq.r2dbc.model.response.PostSummary;
import com.example.jooq.r2dbc.service.PostService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class PostHandler {
    private final PostService postService;

    @Loggable
    public Mono<ServerResponse> getAll(ServerRequest req) {
        return ok().body(this.postService.findAll(), PostSummary.class);
    }

    @Loggable
    public Mono<ServerResponse> get(ServerRequest req) {
        return this.postService
                .findById(req.pathVariable("id"))
                .flatMap(post -> ServerResponse.ok().body(Mono.just(post), Post.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Loggable
    public Mono<ServerResponse> create(ServerRequest req) {
        return req.bodyToMono(CreatePostCommand.class)
                .flatMap(this.postService::create)
                .flatMap(id -> created(URI.create("/posts/" + id)).build());
    }

    @Loggable
    public Mono<ServerResponse> update(ServerRequest req) {

        return Mono.zip(
                        (data) -> {
                            Post p = (Post) data[0];
                            Post p2 = (Post) data[1];
                            p.setTitle(p2.getTitle());
                            p.setContent(p2.getContent());
                            return p;
                        },
                        this.postService.findById(req.pathVariable("id")),
                        req.bodyToMono(Post.class))
                .cast(Post.class)
                .flatMap(this.postService::save)
                .flatMap(post -> ServerResponse.noContent().build());
    }

    @Loggable
    public Mono<ServerResponse> search(ServerRequest req) {
        return postService
                .findByKeyword(
                        req.queryParam("keyword").orElse(""),
                        req.queryParam("offset").map(Integer::parseInt).orElse(0),
                        req.queryParam("limit").map(Integer::parseInt).orElse(10))
                .flatMap(
                        paginatedResult ->
                                ServerResponse.ok()
                                        .body(Mono.just(paginatedResult), PaginatedResult.class));
    }

    @Loggable
    public Mono<ServerResponse> createComments(ServerRequest req) {

        return req.bodyToMono(CreatePostComment.class)
                .flatMap(
                        createPostComment ->
                                postService.addCommentToPostId(
                                        req.pathVariable("id"), createPostComment))
                .flatMap(id -> created(URI.create("/posts/comments/" + id)).build());
    }
}

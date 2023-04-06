package com.example.graphql.services;

import com.example.graphql.entities.PostCommentEntity;
import com.example.graphql.repositories.PostCommentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;

    public List<PostCommentEntity> findAllPostComments() {
        return postCommentRepository.findAll();
    }

    public Optional<PostCommentEntity> findPostCommentById(Long id) {
        return postCommentRepository.findById(id);
    }

    public PostCommentEntity savePostComment(PostCommentEntity postCommentEntity) {
        return postCommentRepository.save(postCommentEntity);
    }

    public void deletePostCommentById(Long id) {
        postCommentRepository.deleteById(id);
    }

    public Map<Long, List<PostCommentEntity>> getCommentsByPostIdIn(List<Long> postIds) {
        return this.postCommentRepository.findByPostEntity_IdIn(postIds).stream()
                .collect(Collectors.groupingBy(postComment -> postComment.getPostEntity().getId()));
    }
}

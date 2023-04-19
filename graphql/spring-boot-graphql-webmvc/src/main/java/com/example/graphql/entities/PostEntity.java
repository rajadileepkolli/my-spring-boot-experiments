package com.example.graphql.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.Hibernate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "posts")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class PostEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;

    @Column(length = 4096)
    private String content;

    private boolean published;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private LocalDateTime publishedAt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "postEntity", orphanRemoval = true)
    @Builder.Default
    private List<PostCommentEntity> comments = new ArrayList<>();

    @OneToOne(
            mappedBy = "postEntity",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = false)
    private PostDetailsEntity details;

    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostTagEntity> tags = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private AuthorEntity authorEntity;

    public PostEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public void addComment(PostCommentEntity comment) {
        this.comments.add(comment);
        comment.setPostEntity(this);
    }

    public void removeComment(PostCommentEntity comment) {
        this.comments.remove(comment);
        comment.setPostEntity(null);
    }

    public void setDetails(PostDetailsEntity details) {
        if (details == null) {
            if (this.details != null) {
                this.details.setPostEntity(null);
            }
        } else {
            details.setPostEntity(this);
        }
        this.details = details;
    }

    public void addTag(TagEntity tagEntity) {
        PostTagEntity postTagEntity = new PostTagEntity(this, tagEntity);
        if (null == tags) {
            tags = new ArrayList<>();
        }
        this.tags.add(postTagEntity);
    }

    public void removeTag(TagEntity tagEntity) {
        for (Iterator<PostTagEntity> iterator = this.tags.iterator(); iterator.hasNext(); ) {
            PostTagEntity postTagEntity = iterator.next();

            if (postTagEntity.getPostEntity().equals(this)
                    && postTagEntity.getTagEntity().equals(tagEntity)) {
                iterator.remove();
                postTagEntity.setPostEntity(null);
                postTagEntity.setTagEntity(null);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PostEntity postEntity = (PostEntity) o;
        return id != null
                && title != null
                && Objects.equals(id, postEntity.id)
                && Objects.equals(this.title, postEntity.title);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

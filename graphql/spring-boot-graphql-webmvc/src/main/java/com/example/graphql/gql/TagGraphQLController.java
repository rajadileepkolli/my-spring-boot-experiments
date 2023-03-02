package com.example.graphql.gql;

import com.example.graphql.entities.TagEntity;
import com.example.graphql.services.TagService;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@Validated
@Slf4j
@RequiredArgsConstructor
public class TagGraphQLController {

    private final TagService tagService;

    @QueryMapping
    public List<TagEntity> allTags() {
        return this.tagService.findAllTags();
    }

    @QueryMapping
    public Optional<TagEntity> findTagByName(@Argument("tagName") String tagName) {
        return this.tagService.findTagByName(tagName);
    }

    @MutationMapping
    public TagEntity createTag(
            @NotBlank @Argument("tagName") String tagName,
            @Argument("tagDescription") String tagDescription) {
        return this.tagService.saveTag(tagName, tagDescription);
    }

    @MutationMapping
    public Optional<TagEntity> updateTagDescription(
            @NotBlank @Argument("tagName") String tagName,
            @NotBlank @Argument("tagDescription") String tagDescription) {
        return this.tagService.updateTag(tagName, tagDescription);
    }

    @MutationMapping
    public boolean deleteTag(@NotBlank @Argument("tagName") String tagName) {
        this.tagService.deleteTagByName(tagName);
        log.info("Deleted tag with Name :{}", tagName);
        return true;
    }
}

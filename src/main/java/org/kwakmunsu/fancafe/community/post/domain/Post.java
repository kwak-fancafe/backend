package org.kwakmunsu.fancafe.community.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.fancafe.community.category.domain.Category;
import org.kwakmunsu.fancafe.global.support.BaseEntity;
import org.kwakmunsu.fancafe.global.support.error.CoreException;
import org.kwakmunsu.fancafe.global.support.error.ErrorType;
import org.kwakmunsu.fancafe.member.domain.Member;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
public class Post extends BaseEntity {

    private static final int TITLE_MAX_LENGTH = 200;
    private static final int CONTENT_MAX_LENGTH = 50_000;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus postStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Category category;

    public static Post create(
            String title,
            String content,
            PostStatus postStatus,
            Member author,
            Category category
    ) {
        Post post = new Post();

        validateTitle(title);
        validateContent(content);

        post.title = title;
        post.content = content;
        post.postStatus = postStatus;
        post.author = author;
        post.category = category;

        return post;
    }

    public void saveDraft() {
        this.postStatus = PostStatus.DRAFT;
    }

    public void publish() {
        if (postStatus != PostStatus.DRAFT && postStatus != PostStatus.PRIVATE) {
            throw new CoreException(ErrorType.POST_CANNOT_PUBLISH);
        }
        this.postStatus = PostStatus.PUBLIC;
    }

    public void hide() {
        if (postStatus != PostStatus.PUBLIC) {
            throw new CoreException(ErrorType.POST_CANNOT_HIDE);
        }
        this.postStatus = PostStatus.PRIVATE;
    }

    public void update(
            String title,
            String content,
            Category category
    ) {
        validateTitle(title);
        validateContent(content);

        this.title = title;
        this.content = content;
        this.category = category;
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank() || title.length() > TITLE_MAX_LENGTH) {
            throw new CoreException(ErrorType.POST_INVALID_TITLE);
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.length() > CONTENT_MAX_LENGTH) {
            throw new CoreException(ErrorType.POST_INVALID_CONTENT);
        }
    }

}

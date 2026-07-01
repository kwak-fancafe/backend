package org.kwakmunsu.fancafe.community.post.application.dto;

import lombok.Builder;
import org.kwakmunsu.fancafe.community.post.domain.PostStatus;

@Builder
public record NewPost(
        Long categoryId,
        String title,
        String content,
        PostStatus status
) {

}
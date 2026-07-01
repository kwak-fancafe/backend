package org.kwakmunsu.fancafe.community.post.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.kwakmunsu.fancafe.community.post.application.dto.NewPost;
import org.kwakmunsu.fancafe.community.post.domain.PostStatus;

@Schema(description = "게시물 등록 요청 DTO")
public record PostRegisterRequest(
        @Schema(description = "게시물 제목", example = "게시물 제목입니다.")
        @NotBlank(message = "게시물 제목은 필수 입력 값입니다.")
        String title,

        @Schema(description = "게시물 내용", example = "게시물 내용입니다.")
        @NotBlank(message = "게시물 내용은 필수 입력 값입니다.")
        String content,

        @Schema(description = "게시물 상태", example = "PUBLIC")
        @NotNull(message = "게시물 상태는 필수 입력 값입니다.")
        PostStatus postStatus,

        @Schema(description = "게시물 카테고리 ID", example = "1")
        @NotNull(message = "카테고리 ID는 필수 입력 값입니다.")
        Long categoryId
) {

    public NewPost newPost() {
        return NewPost.builder()
                .title(title)
                .content(content)
                .status(postStatus)
                .categoryId(categoryId)
                .build();
    }

}
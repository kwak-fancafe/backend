package org.kwakmunsu.fancafe.member.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원 프로필 수정 요청 DTO")
public record MemberProfileUpdateRequest(
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Schema(description = "닉네임", example = "팬카페닉네임")
        String nickname
) {

}
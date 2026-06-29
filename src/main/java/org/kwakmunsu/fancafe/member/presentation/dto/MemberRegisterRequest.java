package org.kwakmunsu.fancafe.member.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.kwakmunsu.fancafe.member.application.dto.NewMember;

@Schema(description = "회원 등록 요청 DTO")
public record MemberRegisterRequest(
        @Schema(description = "로그인 ID", example = "user123")
        @NotBlank(message = "로그인 ID는 필수 입력 값입니다.")
        String loginId,

        @Schema(description = "비밀번호", example = "password123")
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        String password,

        @Schema(description = "닉네임", example = "닉네임")
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        String nickname
) {

    public NewMember toNewMember() {
        return NewMember.builder()
                .loginId(loginId)
                .password(password)
                .nickname(nickname)
                .build();
    }

}
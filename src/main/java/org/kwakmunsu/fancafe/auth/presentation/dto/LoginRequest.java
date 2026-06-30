package org.kwakmunsu.fancafe.auth.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "로그인 요청 DTO", description = "사용자가 로그인 시 필요한 정보를 담은 DTO")
public record LoginRequest(
        @NotBlank(message = "로그인 아이디를 입력해주세요.")
        @Schema(description = "로그인 아이디", example = "test1234")
        String loginId,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "비밀번호", example = "test1234")
        String password
) {

}
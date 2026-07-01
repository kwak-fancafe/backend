package org.kwakmunsu.fancafe.member.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.kwakmunsu.fancafe.member.domain.Member;
import org.kwakmunsu.fancafe.member.domain.Role;

@Builder(access = lombok.AccessLevel.PRIVATE)
@Schema(description = "회원 프로필 정보")
public record MemberProfile(
        @Schema(description = "회원 ID", example = "1")
        Long id,

        @Schema(description = "회원 닉네임", example = "munsu")
        String nickname,

        @Schema(description = "회원 로그인 ID", example = "munsu123")
        String loginId,

        @Schema(description = "회원 권한", example = "ROLE_FAN")
        Role role,

        @Schema(description = "회원 가입일", example = "2023-01-01T12:00:00")
        LocalDateTime createdAt

) {

    public static MemberProfile from(Member member) {
        return MemberProfile.builder()
                .id(member.getId())
                .nickname(member.getNickname().value())
                .loginId(member.getLoginId().value())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .build();
    }

}
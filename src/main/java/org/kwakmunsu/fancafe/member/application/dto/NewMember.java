package org.kwakmunsu.fancafe.member.application.dto;

import lombok.Builder;

@Builder
public record NewMember(
        String loginId,
        String password,
        String nickname
) {

}
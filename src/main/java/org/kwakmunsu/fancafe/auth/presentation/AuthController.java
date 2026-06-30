package org.kwakmunsu.fancafe.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.fancafe.auth.application.AuthFacade;
import org.kwakmunsu.fancafe.auth.presentation.dto.LoginRequest;
import org.kwakmunsu.fancafe.auth.presentation.dto.ReissueRequest;
import org.kwakmunsu.fancafe.global.security.annotation.LoginMember;
import org.kwakmunsu.fancafe.global.security.dto.TokenResponse;
import org.kwakmunsu.fancafe.global.support.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController extends AuthControllerDocs {

    private final AuthFacade authFacade;

    @PostMapping("/api/v1/auth/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse response = authFacade.login(request.loginId(), request.password());

        return ApiResponse.success(response);
    }

    @PostMapping("/api/v1/auth/reissue")
    public ApiResponse<TokenResponse> reissue(@RequestBody @Valid ReissueRequest request) {
        TokenResponse response = authFacade.reissue(request.refreshToken());

        return ApiResponse.success(response);
    }

    @PostMapping("/api/v1/auth/logout")
    public ApiResponse<?> logout(@LoginMember Long memberId) {
        authFacade.logout(memberId);

        return ApiResponse.success();
    }

}

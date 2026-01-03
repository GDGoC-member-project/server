package com.gdgoc.member.account;

import com.gdgoc.member.account.dto.AccountResponse;
import com.gdgoc.member.common.api.BaseResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account")
    public BaseResponse<AccountResponse> meAccount(@AuthenticationPrincipal OidcUser principal) {
        String subject = principal.getSubject();
        String email = principal.getEmail();
        String externalUid = "google:" + subject;

        UserAuth userAuth = accountService.getOrCreate(externalUid, email);

        AccountResponse payload = new AccountResponse(
                userAuth.getUserId(),
                userAuth.getFirebaseUid(),
                userAuth.getEmail(),
                userAuth.getRole().name()
        );

        return BaseResponse.success(payload);
    }
}


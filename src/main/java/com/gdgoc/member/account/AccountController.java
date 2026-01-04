package com.gdgoc.member.account;

import com.gdgoc.member.account.dto.AccountResponse;
import com.gdgoc.member.common.api.BaseResponse;
import com.gdgoc.member.security.CurrentUser;
import com.gdgoc.member.security.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class AccountController {

    private final CurrentUserService currentUserService;

    public AccountController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/account")
    public BaseResponse<AccountResponse> meAccount() {
        CurrentUser currentUser = currentUserService.requireUser();

        AccountResponse payload = new AccountResponse(
                currentUser.userId(),
                currentUser.externalUid(),
                currentUser.email(),
                currentUser.role().name()
        );

        return BaseResponse.success(payload);
    }
}


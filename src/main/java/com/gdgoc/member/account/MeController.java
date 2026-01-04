package com.gdgoc.member.account;

import com.gdgoc.member.common.api.BaseResponse;
import com.gdgoc.member.security.CurrentUser;
import com.gdgoc.member.security.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {

    private final CurrentUserService currentUserService;

    public MeController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public BaseResponse<Map<String, Object>> me() {
        CurrentUser currentUser = currentUserService.requireUser();

        return BaseResponse.success(Map.of(
                "subject", currentUser.subject(),
                "email", currentUser.email()
        ));
    }
}

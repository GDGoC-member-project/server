package com.gdgoc.member.security;

import com.gdgoc.member.account.AccountService;
import com.gdgoc.member.account.UserAuth;
import com.gdgoc.member.global.error.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final AccountService accountService;

    public CurrentUserService(AccountService accountService) {
        this.accountService = accountService;
    }

    public CurrentUser requireUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof OidcUser oidcUser)) {
            throw new UnauthorizedException();
        }

        String subject = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String externalUid = "google:" + subject;

        UserAuth userAuth = accountService.getOrCreate(externalUid, email);

        return new CurrentUser(
                userAuth.getUserId(),
                subject,
                userAuth.getExternalUid(),
                userAuth.getEmail(),
                userAuth.getRole()
        );
    }
}
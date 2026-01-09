package com.gdgoc.member.security;

import com.gdgoc.member.account.UserAuth;
import com.gdgoc.member.account.UserAuthRepository;
import com.gdgoc.member.global.error.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserAuthRepository userAuthRepository;

    public CurrentUserService(UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
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

        UserAuth userAuth = userAuthRepository.findByExternalUid(subject)
                .orElseThrow(() -> new IllegalStateException("User not found: " + subject));

        return new CurrentUser(
                userAuth.getUserId(),
                subject,
                userAuth.getExternalUid(),
                userAuth.getEmail(),
                userAuth.getRole()
        );
    }
}

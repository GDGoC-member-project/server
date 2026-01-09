package com.gdgoc.member.security;

import com.gdgoc.member.account.UserAuth;
import com.gdgoc.member.account.UserAuthRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserAuthRepository userAuthRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        UserAuth userAuth = userAuthRepository.findByExternalUid(oidcUser.getName()).orElseThrow();

        ClassPathResource resource = new ClassPathResource("static/oauth_success.html");
        Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        String htmlContent = FileCopyUtils.copyToString(reader);

        htmlContent = htmlContent.replace("[[${userId}]]", userAuth.getUserId().toString());
        htmlContent = htmlContent.replace("[[${externalUid}]]", userAuth.getExternalUid());
        htmlContent = htmlContent.replace("[[${email}]]", userAuth.getEmail());
        htmlContent = htmlContent.replace("[[${role}]]", userAuth.getRole().name());

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(htmlContent);
    }
}

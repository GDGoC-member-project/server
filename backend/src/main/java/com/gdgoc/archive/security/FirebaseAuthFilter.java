package com.gdgoc.archive.security;

import com.gdgoc.archive.auth.FirebaseTokenVerifier;
import com.gdgoc.archive.auth.FirebaseVerifiedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    public static final String ATTR_FIREBASE_UID = "firebaseUid";
    public static final String ATTR_FIREBASE_EMAIL = "firebaseEmail";

    private final FirebaseTokenVerifier verifier;

    @Value("${auth.firebase.enabled:true}")
    private boolean authEnabled;

    public FirebaseAuthFilter(FirebaseTokenVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/v1/me/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ✅ local/dev test bypass (no Firebase ID token)
        if (!authEnabled) {
            String uid = request.getHeader("X-Debug-Firebase-Uid");
            String email = request.getHeader("X-Debug-Firebase-Email");

            request.setAttribute(ATTR_FIREBASE_UID, (uid == null || uid.isBlank()) ? "debug-uid" : uid);
            request.setAttribute(ATTR_FIREBASE_EMAIL, (email == null || email.isBlank()) ? null : email);

            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response);
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            writeUnauthorized(response);
            return;
        }

        try {
            FirebaseVerifiedUser verified = verifier.verify(token);

            request.setAttribute(ATTR_FIREBASE_UID, verified.uid());
            request.setAttribute(ATTR_FIREBASE_EMAIL, verified.email());

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            writeUnauthorized(response);
        }
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        // BaseResponse<Void> ERROR 형태로 고정
        response.getWriter().write(
                "{\"status\":\"ERROR\",\"data\":null," +
                        "\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"Invalid or missing token.\",\"details\":null}}"
        );
    }
}

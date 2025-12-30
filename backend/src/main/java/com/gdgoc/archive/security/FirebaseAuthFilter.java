package com.gdgoc.archive.security;

import com.gdgoc.archive.auth.FirebaseTokenVerifier;
import com.gdgoc.archive.auth.FirebaseVerifiedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    public static final String ATTR_FIREBASE_UID = "firebaseUid";
    public static final String ATTR_FIREBASE_EMAIL = "firebaseEmail";

    private final FirebaseTokenVerifier verifier;

    public FirebaseAuthFilter(FirebaseTokenVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Protect only /me/** (public APIs remain open)
        return !path.startsWith("/me/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

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

            // set request attributes for downstream controllers/services
            request.setAttribute(ATTR_FIREBASE_UID, verified.uid());
            request.setAttribute(ATTR_FIREBASE_EMAIL, verified.email());

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // e.printStackTrace();
            writeUnauthorized(response);
        }
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Invalid or missing token.\"}");
    }
}

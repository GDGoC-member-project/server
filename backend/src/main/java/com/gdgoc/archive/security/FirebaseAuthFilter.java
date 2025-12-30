package com.gdgoc.archive.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdgoc.archive.auth.FirebaseTokenVerifier;
import com.gdgoc.archive.error.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    public static final String ATTR_FIREBASE_UID = "firebaseUid";

    private final FirebaseTokenVerifier verifier;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FirebaseAuthFilter(FirebaseTokenVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/me/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response);
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        try {
            String uid = verifier.verifyAndGetUid(token);
            request.setAttribute(ATTR_FIREBASE_UID, uid);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            writeUnauthorized(response);
        }
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getWriter(),
                new ErrorResponse("UNAUTHORIZED", "Invalid or missing token.")
        );
    }
}
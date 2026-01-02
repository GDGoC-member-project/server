package com.gdgoc.archive.auth;

import com.gdgoc.archive.common.api.BaseResponse;
import com.gdgoc.archive.auth.dto.PingResponse;
import com.gdgoc.archive.security.FirebaseAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class AuthTestController {

    @GetMapping("/ping")
    public BaseResponse<PingResponse> ping(HttpServletRequest request) {
        String firebaseUid = (String) request.getAttribute(FirebaseAuthFilter.ATTR_FIREBASE_UID);
        String email = (String) request.getAttribute(FirebaseAuthFilter.ATTR_FIREBASE_EMAIL);

        PingResponse payload = new PingResponse(true, firebaseUid, email);
        return BaseResponse.success(payload);
    }
}

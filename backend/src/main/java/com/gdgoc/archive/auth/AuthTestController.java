package com.gdgoc.archive.auth;

import com.gdgoc.archive.security.FirebaseAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthTestController {

    @GetMapping("/me/ping")
    public Map<String, Object> ping(HttpServletRequest request) {
        Object uid = request.getAttribute(FirebaseAuthFilter.ATTR_FIREBASE_UID);
        return Map.of("ok", true, "firebaseUid", uid);
    }
}

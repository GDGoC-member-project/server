package com.gdgoc.archive.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Component;

@Component
public class FirebaseTokenVerifier {

    public String verifyAndGetUid(String idToken) {
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return decoded.getUid();
        } catch (Exception e) {
            throw new IllegalArgumentException("INVALID_TOKEN");
        }
    }
}
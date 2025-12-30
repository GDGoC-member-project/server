package com.gdgoc.archive.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Component;

@Component
public class FirebaseTokenVerifier {

    /**
     * Backward-compatible method (kept for existing callers).
     * Returns uid if token is valid; throws IllegalArgumentException("INVALID_TOKEN") otherwise.
     */
    public String verifyAndGetUid(String idToken) {
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return decoded.getUid();
        } catch (Exception e) {
            throw new IllegalArgumentException("INVALID_TOKEN", e);
        }
    }

    /**
     * Preferred method: verifies ID token and returns both uid and email (email can be null).
     * Uses a consistent exception type so callers can handle invalid tokens uniformly.
     */
    public FirebaseVerifiedUser verify(String idToken) {
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decoded.getUid();
            String email = decoded.getEmail(); // may be null depending on sign-in method
            return new FirebaseVerifiedUser(uid, email);
        } catch (FirebaseAuthException e) {
            // Firebase-specific verification errors
            throw new IllegalArgumentException("INVALID_TOKEN", e);
        } catch (Exception e) {
            // Any other unexpected errors
            throw new IllegalArgumentException("INVALID_TOKEN", e);
        }
    }
}

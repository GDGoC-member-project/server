package com.gdgoc.archive.account;

import java.time.Instant;

public record UserAuthDoc(
        String userId,
        String firebaseUid,
        String email,
        String role,
        String passwordHash,
        Instant createdAt,
        Instant updatedAt
) {}

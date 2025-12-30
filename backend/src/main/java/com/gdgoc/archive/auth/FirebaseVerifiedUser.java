package com.gdgoc.archive.auth;

public record FirebaseVerifiedUser(
        String uid,
        String email
) {}

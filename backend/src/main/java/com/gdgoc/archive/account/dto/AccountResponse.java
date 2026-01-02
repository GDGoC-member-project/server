package com.gdgoc.archive.account.dto;

public record AccountResponse(
        String userId,
        String firebaseUid,
        String email,
        String role
) {}

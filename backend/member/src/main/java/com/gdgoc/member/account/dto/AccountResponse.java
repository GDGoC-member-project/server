package com.gdgoc.member.account.dto;

public record AccountResponse(
        String userId,
        String firebaseUid,
        String email,
        String role
) {}

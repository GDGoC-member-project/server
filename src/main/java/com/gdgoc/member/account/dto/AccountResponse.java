package com.gdgoc.member.account.dto;

public record AccountResponse(
        String userId,
        String externalUid,
        String email,
        String role
) {}
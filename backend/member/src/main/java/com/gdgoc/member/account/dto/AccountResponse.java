package com.gdgoc.member.account.dto;

import java.util.UUID;

public record AccountResponse(
        UUID userId,
        String externalUid,
        String email,
        String role
) {}
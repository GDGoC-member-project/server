package com.gdgoc.member.security;

import java.util.UUID;

import com.gdgoc.member.account.Role;

public record CurrentUser(
        UUID userId,
        String subject,
        String externalUid,
        String email,
        Role role
) {
}


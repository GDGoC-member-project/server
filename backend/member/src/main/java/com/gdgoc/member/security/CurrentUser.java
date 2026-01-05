package com.gdgoc.member.security;

import com.gdgoc.member.account.Role;

import java.util.UUID;

public record CurrentUser(
        UUID userId,
        String subject,
        String externalUid,
        String email,
        Role role
) {
}
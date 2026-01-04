package com.gdgoc.member.security;

import com.gdgoc.member.account.Role;

public record CurrentUser(
        String userId,
        String subject,
        String externalUid,
        String email,
        Role role
) {
}


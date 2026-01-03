package com.gdgoc.member.auth.dto;

public record PingResponse(
        boolean ok,
        String firebaseUid,
        String email
) {}

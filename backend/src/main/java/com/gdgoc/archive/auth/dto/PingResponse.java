package com.gdgoc.archive.auth.dto;

public record PingResponse(
        boolean ok,
        String firebaseUid,
        String email
) {}

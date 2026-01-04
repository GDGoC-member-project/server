package com.gdgoc.member.common.api;

public record ApiError(
        String code,
        String message,
        Object details
) {}

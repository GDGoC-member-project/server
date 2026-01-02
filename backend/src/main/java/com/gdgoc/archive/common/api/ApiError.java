package com.gdgoc.archive.common.api;

public record ApiError(
        String code,
        String message,
        Object details
) {}
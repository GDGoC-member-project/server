package com.gdgoc.member.global.error;

public class ErrorResponse {
    private final String code;
    private final String message;
    private final Object details; // optional

    public ErrorResponse(String code, String message, Object details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, null);
    }

    public static ErrorResponse of(String code, String message, Object details) {
        return new ErrorResponse(code, message, details);
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Object getDetails() { return details; }
}


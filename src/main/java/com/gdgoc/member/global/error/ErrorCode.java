package com.gdgoc.member.global.error;

public enum ErrorCode {
    INVALID_REQUEST("INVALID_REQUEST", "요청 값이 올바르지 않습니다."),
    PROFILE_NOT_FOUND("PROFILE_NOT_FOUND", "프로필을 찾을 수 없습니다."),
    DUPLICATE_USER_ID("DUPLICATE_USER_ID", "이미 등록된 사용자입니다."),
    SERVER_ERROR("SERVER_ERROR", "서버 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() { return code; }
    public String message() { return message; }
}

package com.gdgoc.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class BaseResponse<T> {
    private String status;
    private T data;
    private ErrorBody error;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>("SUCCESS", data, null);
    }

    public static BaseResponse<Void> success() {
        return new BaseResponse<>("SUCCESS", null, null);
    }

    public static <T> BaseResponse<T> error(String code, String message, Object details) {
        return new BaseResponse<>("ERROR", null, new ErrorBody(code, message, details));
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorBody {
        private String code;
        private String message;
        private Object details; // optional
    }
}


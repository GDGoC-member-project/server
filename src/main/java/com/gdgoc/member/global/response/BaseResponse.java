package com.gdgoc.member.global.response;

import com.gdgoc.member.global.error.ErrorResponse;

public class BaseResponse<T> {

    private final String status; // SUCCESS | ERROR
    private final T data;        // 성공 시 payload
    private final ErrorResponse error; // 실패 시 error

    private BaseResponse(String status, T data, ErrorResponse error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>("SUCCESS", data, null);
    }

    public static <T> BaseResponse<T> error(ErrorResponse error) {
        return new BaseResponse<>("ERROR", null, error);
    }

    public String getStatus() { return status; }
    public T getData() { return data; }
    public ErrorResponse getError() { return error; }
}

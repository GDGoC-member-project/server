package com.gdgoc.archive.common.api;

public record BaseResponse<T>(
        String status,
        T data,
        ApiError error
) {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>("SUCCESS", data, null);
    }

    public static <T> BaseResponse<T> error(String code, String message) {
        return new BaseResponse<>("ERROR", null, new ApiError(code, message, null));
    }

    public static <T> BaseResponse<T> error(String code, String message, Object details) {
        return new BaseResponse<>("ERROR", null, new ApiError(code, message, details));
    }
}

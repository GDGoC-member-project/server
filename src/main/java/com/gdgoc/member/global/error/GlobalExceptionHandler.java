package com.gdgoc.member.global.error;

import com.gdgoc.member.global.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =========================
     * 1) 인증 예외: 401
     * ========================= */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseResponse<Void>> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.error(ErrorResponse.of(
                        "UNAUTHORIZED",
                        "Invalid or missing token."
                )));
    }

    /**
     * verifier에서 INVALID_TOKEN을 IllegalArgumentException으로 던질 수 있으므로,
     * 이 케이스는 401로 매핑합니다.
     * 그 외 IllegalArgumentException은 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        if ("INVALID_TOKEN".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error(ErrorResponse.of(
                            "UNAUTHORIZED",
                            "Invalid or missing token."
                    )));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(ErrorResponse.of(
                        ErrorCode.INVALID_REQUEST.code(),
                        e.getMessage()
                )));
    }

    /* =========================
     * 2) 도메인/비즈니스 예외: (ErrorCode에 맞게 상태코드 내려주기)
     *    - 지금은 ErrorCode가 code/message만 있는 형태라 400으로 처리
     *    - 만약 ErrorCode에 httpStatus가 있으면 그걸로 바꾸면 됨
     * ========================= */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<BaseResponse<Void>> handleApi(ApiException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(ErrorResponse.of(
                        e.getErrorCode().code(),
                        e.getErrorCode().message(),
                        e.getDetails()
                )));
    }

    /* =========================
     * 3) Validation 예외: 400
     * ========================= */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(ErrorResponse.of(
                        ErrorCode.INVALID_REQUEST.code(),
                        ErrorCode.INVALID_REQUEST.message(),
                        e.getBindingResult().getFieldErrors().stream()
                                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                                .toList()
                )));
    }

    /* =========================
     * 4) 그 외: 500
     * ========================= */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleAll(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(ErrorResponse.of(
                        ErrorCode.SERVER_ERROR.code(),
                        ErrorCode.SERVER_ERROR.message()
                )));
    }
}

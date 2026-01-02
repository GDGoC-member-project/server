package com.gdgoc.archive.error;

import com.gdgoc.archive.common.api.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseResponse<Void>> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.error("UNAUTHORIZED", "Invalid or missing token."));
    }

    /**
     * verifier에서 INVALID_TOKEN을 IllegalArgumentException으로 던질 수 있으므로,
     * 이 케이스는 401로 매핑합니다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        if ("INVALID_TOKEN".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.error("UNAUTHORIZED", "Invalid or missing token."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error("BAD_REQUEST", e.getMessage()));
    }

    /**
     * 예상 못한 서버 내부 오류용(최소)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("INTERNAL_ERROR", e.getMessage()));
    }
}

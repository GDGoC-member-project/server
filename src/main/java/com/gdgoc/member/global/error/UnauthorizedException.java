package com.gdgoc.member.global.error;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Invalid or missing token.");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}

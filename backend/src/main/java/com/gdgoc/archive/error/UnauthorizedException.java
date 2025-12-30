package com.gdgoc.archive.error;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Invalid or missing token.");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}

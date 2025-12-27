package com.crypto.tracker.dto.response;

public class RegistrationResponse {

    private Long userId;
    private String message;

    public RegistrationResponse(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}


package com.findataagg.alertconfigservice.users.dto;

public record AuthenticationResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn
) {
    public AuthenticationResponse(String accessToken, String refreshToken, long expiresIn) {
        this(accessToken, refreshToken, "Bearer", expiresIn);
    }
}

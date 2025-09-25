package com.findataagg.alertconfigservice.users.controller;

import com.findataagg.alertconfigservice.config.JwtService;
import com.findataagg.alertconfigservice.users.dto.AuthenticationRequest;
import com.findataagg.alertconfigservice.users.dto.AuthenticationResponse;
import com.findataagg.alertconfigservice.users.dto.RefreshTokenRequest;
import com.findataagg.alertconfigservice.users.dto.UserRegistrationRequest;
import com.findataagg.alertconfigservice.users.service.UserService;
import com.findataagg.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        User registeredUser = userService.registerUser(request);
        return new ResponseEntity<>("User registered successfully with ID: " + registeredUser.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        final String accessToken = jwtService.generateToken(userDetails);
        final String refreshToken = jwtService.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(
                accessToken,
                refreshToken,
                jwtService.getJwtExpiration()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        final String username = jwtService.extractUsername(request.refreshToken());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(request.refreshToken(), userDetails)) {
            // Check if the session has exceeded the absolute maximum duration
            if (!jwtService.isSessionValid(request.refreshToken())) {
                throw new SecurityException("Session expired, please login again");
            }

            // Extract original login time to preserve it in new refresh token
            Long originalLoginTime = jwtService.extractOriginalLoginTime(request.refreshToken());

            final String newAccessToken = jwtService.generateToken(userDetails);
            final String newRefreshToken = jwtService.generateRefreshToken(userDetails, originalLoginTime);

            return ResponseEntity.ok(new AuthenticationResponse(
                    newAccessToken,
                    newRefreshToken,
                    jwtService.getJwtExpiration()
            ));
        }

        throw new SecurityException("Invalid refresh token");
    }
}

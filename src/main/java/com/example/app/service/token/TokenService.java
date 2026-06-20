package com.example.app.service.token;

import com.example.app.dto.response.GetUserAndToken;
import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import com.example.app.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private static final int REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 10; // 10 days

    /**
     * Generate tokens and set refresh token cookie
     */
    public GetUserAndToken generateTokensAndSetCookie(User user, HttpServletResponse response) {
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        setRefreshTokenCookie(refreshToken, response);

        return GetUserAndToken.builder()
                .user(user)
                .accessToken(accessToken)
                .build();
    }

    /**
     * Set refresh token as cookie
     */
    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // development
                .path("/")
                .maxAge(REFRESH_TOKEN_MAX_AGE)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}


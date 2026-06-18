package com.example.app.service;

import com.example.app.dto.request.UserLoginRequest;
import com.example.app.dto.request.UserRegisterRequest;
import com.example.app.dto.response.GetUserAndToken;
import com.example.app.exception.DuplicateResourceException;
import com.example.app.model.TokenBlacklist;
import com.example.app.model.User;
import com.example.app.repository.TokenBlacklistRepository;
import com.example.app.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    /*
     * Login user
     * generate access & refresh token
     * save refresh token in db
     * send refresh token in cookie
     */
    public GetUserAndToken login(UserLoginRequest req, HttpServletResponse response) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = userRepository.findByEmail(req.getEmail()).orElseThrow(() -> new BadCredentialsException("Invalid Email or Password"));

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 10)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return GetUserAndToken.builder()
                .user(user)
                .accessToken(accessToken)
                .build();
    }

    /*
     * Register user
     * generate access & refresh token
     * save refresh token in db
     * send refresh token in cookie
     */
    public GetUserAndToken register(UserRegisterRequest req, HttpServletResponse response) throws Exception {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("An account already exists with this email");
        }

        User newUser = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

        User user = userRepository.save(newUser);

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 10)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return GetUserAndToken.builder()
                .user(user)
                .accessToken(accessToken)
                .build();
    }

    /*
     * validate refresh token
     * generate new access token & refresh token
     * save new refresh token in db
     * save old refresh token in blacklist
     * send new refresh token in cookie
     */
    public String refreshAccessToken (String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new BadCredentialsException("Refresh Token is empty");
        }

        if (tokenBlacklistRepository.existsByToken(refreshToken)) {
            throw new BadCredentialsException("Invalid or Expired Refresh Token");
        }

        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Invalid or Expired Refresh Token"));

        String newAccessToken = jwtService.generateAccessToken(email);
        String newRefreshToken = jwtService.generateRefreshToken(email);
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        saveTokenInBlacklist(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 10)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return newAccessToken;
    }

    /*
     * Logout
     * save both access & refresh tokens in blacklist
     */
    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            throw new BadCredentialsException("Access token is empty");
        }

        String token = authHeader.substring(7);
        if (token == null) {
            throw new BadCredentialsException("Access token is empty");
        }

        saveTokenInBlacklist(token);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh_token")) {
                    saveTokenInBlacklist(cookie.getValue());
                }
            }
        }
    }

    /*
     * save token in blacklist
     */
    public void saveTokenInBlacklist(String token) {
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken(token);
        tokenBlacklistRepository.save(tokenBlacklist);
    }
}

package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.dto.request.UserLoginRequest;
import com.example.app.dto.request.UserRegisterRequest;
import com.example.app.dto.response.GetUserAndToken;
import com.example.app.dto.response.UserResponse;
import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import com.example.app.service.AuthService;
import com.example.app.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    /*
     * Login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(
        @Valid @RequestBody UserLoginRequest userLoginRequest,
        HttpServletResponse response
    ) {
        GetUserAndToken userAndToken = authService.login(userLoginRequest, response);
        UserResponse userResponse = UserResponse.userResponseWithToken(userAndToken.getUser(), userAndToken.getAccessToken());
        ApiResponse<UserResponse> res = ApiResponse.successWithContent("User login successful.", userResponse);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /*
     * Register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register (
        @Valid @RequestBody UserRegisterRequest userRegisterRequest, HttpServletResponse response
    ) throws Exception {
        GetUserAndToken userAndToken = authService.register(userRegisterRequest, response);
        UserResponse userResponse = UserResponse.userResponseWithToken(userAndToken.getUser(), userAndToken.getAccessToken());
        ApiResponse<UserResponse> apiResponse = ApiResponse.successWithContent("User registration successful.", userResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    /*
     * Logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
        HttpServletRequest request
    ) {
        authService.logout(request);

        ApiResponse<String> response = ApiResponse.errorWithContent("User logged out successfully.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
     * Refresh Access Token
     */
    @PostMapping("/refresh-access-token")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshAccessToken (
        @CookieValue(name = "refresh_token", required = false) String refreshToken,
        HttpServletResponse response
    ) {
        String accessToken = authService.refreshAccessToken(refreshToken, response);
        ApiResponse<Map<String, String>> apiResponse = ApiResponse.successWithContent("Access token refreshed successfully.", Map.of("access_token", accessToken));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
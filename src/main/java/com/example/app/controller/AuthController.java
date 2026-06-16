package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.dto.request.UserLoginRequest;
import com.example.app.dto.request.UserRegisterRequest;
import com.example.app.dto.response.UserResponse;
import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import com.example.app.service.AuthService;
import com.example.app.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BadCredentialsException("Invalid Email or Password"));
        String token = jwtService.generateToken(user.getEmail());
        UserResponse userResponse = UserResponse.userResponseWithToken(user, token);
        ApiResponse<UserResponse> response = ApiResponse.errorWithContent("User login successful.", userResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register (@Valid @RequestBody UserRegisterRequest request) throws Exception {
        User user = authService.register(request);
        String token = jwtService.generateToken(user.getEmail());
        UserResponse userResponse = UserResponse.userResponseWithToken(user, token);
        ApiResponse<UserResponse> response = ApiResponse.errorWithContent("User registration successful.", userResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        String token = authHeader.substring(7);



        ApiResponse<String> response = ApiResponse.errorWithContent("User logged out successfully.", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
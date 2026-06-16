package com.example.app.controller;

import com.example.app.dto.response.ApiResponse;
import com.example.app.dto.response.UserResponse;
import com.example.app.model.User;
import com.example.app.service.GetCurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final GetCurrentUser getCurrentUser;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile() {
        User user = getCurrentUser.getUser();
        UserResponse userResponse = UserResponse.userResponseWithToken(user, null);
        ApiResponse<UserResponse> response = ApiResponse.successWithContent("User profile fetched successfully", userResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

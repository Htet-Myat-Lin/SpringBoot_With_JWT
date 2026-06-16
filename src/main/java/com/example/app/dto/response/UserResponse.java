package com.example.app.dto.response;

import com.example.app.model.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String username;
    private String role;
    private String token;
    private LocalDateTime createdAt;

    public static UserResponse userResponseWithToken(User user, String token) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())
                .token(token)
                .createdAt(user.getCreatedAt())
                .build();
    }
}

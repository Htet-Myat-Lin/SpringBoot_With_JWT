package com.example.app.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private String message;
    private boolean success;
    private T content;

    public static ApiResponse<String> successWithoutContent(String message) {
        return new ApiResponse<>(message, true, null);
    }

    public static <T> ApiResponse<T> successWithContent(String message, T content) {
        return new ApiResponse<>(message, true, content);
    }

    public static ApiResponse<String> errorWithoutContent(String message) {
        return new ApiResponse<>(message, false, null);
    }

    public static <T> ApiResponse<T> errorWithContent(String message, T content) {
        return new ApiResponse<>(message, false, content);
    }
}

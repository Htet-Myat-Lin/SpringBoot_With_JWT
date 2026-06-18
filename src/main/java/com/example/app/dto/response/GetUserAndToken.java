package com.example.app.dto.response;

import com.example.app.model.User;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserAndToken {
    private User user;
    private String accessToken;
}

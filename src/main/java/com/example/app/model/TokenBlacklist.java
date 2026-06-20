package com.example.app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "token_blacklist")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "token")
    private String token;

    @Column(name = "created_at")
    private Long createdAt;

    @PrePersist
    private void onCreate() {
        createdAt = System.currentTimeMillis();
    }
}
package com.wt.financial_dashboard.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SignupResponse {
    private UUID userId;
    private LocalDateTime createdAt;
}

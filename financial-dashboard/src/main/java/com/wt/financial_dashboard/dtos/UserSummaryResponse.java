package com.wt.financial_dashboard.dtos;

import com.wt.financial_dashboard.util.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserSummaryResponse {
    private UUID userId;
    private String email;
    private Role role;
}

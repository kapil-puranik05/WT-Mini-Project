package com.wt.financial_dashboard.dtos;

import com.wt.financial_dashboard.util.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 15, message = "Password should be 8 to 15 characters long")
    private String password;
    @NotNull(message = "Role is required")
    private Role role;
}

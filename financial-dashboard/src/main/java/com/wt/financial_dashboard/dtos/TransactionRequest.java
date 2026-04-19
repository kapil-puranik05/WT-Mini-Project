package com.wt.financial_dashboard.dtos;

import com.wt.financial_dashboard.util.Category;
import com.wt.financial_dashboard.util.Type;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;
    private BigDecimal amount;
    private Type type;
    private Category category;
    private String description;
    private LocalDateTime date;
}

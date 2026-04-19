package com.wt.financial_dashboard.dtos;

import com.wt.financial_dashboard.models.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    private List<CategorySummary> categoryIncome;
    private List<CategorySummary> categoryExpense;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpense;
    private List<Transaction> recentTransactions;
    private Long transactionCount;
}
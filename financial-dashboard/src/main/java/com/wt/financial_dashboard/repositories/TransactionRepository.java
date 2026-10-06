package com.wt.financial_dashboard.repositories;

import com.wt.financial_dashboard.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query(value = "SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'INCOME'", nativeQuery = true)
    BigDecimal getTotalIncome(UUID userId);

    // 🔹 Total Expense
    @Query(value = "SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'EXPENSE'", nativeQuery = true)
    BigDecimal getTotalExpense(UUID userId);

    // 🔹 Net Balance (Income - Expense)
    @Query(value = "SELECT COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END),0) - COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END),0) FROM transactions WHERE user_id = :userId", nativeQuery = true)
    BigDecimal getNetBalance(UUID userId);

    // 🔹 Category-wise Income
    @Query(value = "SELECT category, SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'INCOME' GROUP BY category", nativeQuery = true)
    List<Object[]> getCategoryWiseIncome(UUID userId);

    // 🔹 Category-wise Expense
    @Query(value = "SELECT category, SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'EXPENSE' GROUP BY category", nativeQuery = true)
    List<Object[]> getCategoryWiseExpense(UUID userId);

    // 🔹 Monthly Expense
    @Query(value = "SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'EXPENSE' AND EXTRACT(MONTH FROM date) = :month AND EXTRACT(YEAR FROM date) = :year", nativeQuery = true)
    BigDecimal getMonthlyExpense(UUID userId, int month, int year);

    // 🔹 Monthly Income
    @Query(value = "SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'INCOME' AND EXTRACT(MONTH FROM date) = :month AND EXTRACT(YEAR FROM date) = :year", nativeQuery = true)
    BigDecimal getMonthlyIncome(UUID userId, int month, int year);

    // 🔹 Recent Transactions (last 5)
    @Query(value = "SELECT * FROM transactions WHERE user_id = :userId ORDER BY date DESC LIMIT 5", nativeQuery = true)
    List<Transaction> getRecentTransactions(UUID userId);

    // 🔹 Transaction Count
    @Query(value = "SELECT COUNT(*) FROM transactions WHERE user_id = :userId", nativeQuery = true)
    Long getTransactionCount(UUID userId);

}

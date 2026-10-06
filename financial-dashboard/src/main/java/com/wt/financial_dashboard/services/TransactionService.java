package com.wt.financial_dashboard.services;

import com.wt.financial_dashboard.dtos.*;
import com.wt.financial_dashboard.exceptions.InvalidCredentialsException;
import com.wt.financial_dashboard.exceptions.OwnershipException;
import com.wt.financial_dashboard.exceptions.UserNotFoundException;
import com.wt.financial_dashboard.models.AppUser;
import com.wt.financial_dashboard.models.Transaction;
import com.wt.financial_dashboard.repositories.TransactionRepository;
import com.wt.financial_dashboard.repositories.UserRepository;
import com.wt.financial_dashboard.util.Role;
import com.wt.financial_dashboard.util.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public Transaction createTransaction(TransactionRequest request) {
        AppUser currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new OwnershipException("Only ADMIN can create transactions");
        }
        AppUser targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Target user was not found"));
        Transaction t = new Transaction();
        t.setAmount(request.getAmount());
        t.setType(request.getType());
        t.setCategory(request.getCategory());
        t.setDescription(request.getDescription());
        t.setDate(request.getDate() != null ? request.getDate() : LocalDateTime.now());
        t.setUser(targetUser);
        return transactionRepository.save(t);
    }

    public DashboardResponse getDashboard(int month, int year) {
        AppUser currentUser = getCurrentUser();
        return switch (currentUser.getRole()) {
            case VIEWER -> viewerDashboard(currentUser.getUserId(), month, year);
            case ANALYST, ADMIN -> systemDashboard(month, year);
        };
    }

    private DashboardResponse viewerDashboard(UUID userId, int month, int year) {
        return DashboardResponse.builder()
                .totalIncome(safe(transactionRepository.getTotalIncome(userId)))
                .totalExpense(safe(transactionRepository.getTotalExpense(userId)))
                .netBalance(safe(transactionRepository.getNetBalance(userId)))
                .categoryIncome(mapToCategory(transactionRepository.getCategoryWiseIncome(userId)))
                .categoryExpense(mapToCategory(transactionRepository.getCategoryWiseExpense(userId)))
                .monthlyIncome(safe(transactionRepository.getMonthlyIncome(userId, month, year)))
                .monthlyExpense(safe(transactionRepository.getMonthlyExpense(userId, month, year)))
                .recentTransactions(transactionRepository.getRecentTransactions(userId))
                .transactionCount(transactionRepository.getTransactionCount(userId))
                .build();
    }

    private DashboardResponse systemDashboard(int month, int year) {
        List<Transaction> all = transactionRepository.findAll();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        BigDecimal monthlyIncome = BigDecimal.ZERO;
        BigDecimal monthlyExpense = BigDecimal.ZERO;
        Map<String, BigDecimal> incomeMap = new HashMap<>();
        Map<String, BigDecimal> expenseMap = new HashMap<>();
        for (Transaction t : all) {
            BigDecimal amt = t.getAmount();
            boolean isIncome = t.getType() == Type.INCOME;
            if (isIncome) {
                totalIncome = totalIncome.add(amt);
                incomeMap.merge(t.getCategory().name(), amt, BigDecimal::add);
            } else {
                totalExpense = totalExpense.add(amt);
                expenseMap.merge(t.getCategory().name(), amt, BigDecimal::add);
            }
            if (t.getDate() != null && t.getDate().getMonthValue() == month && t.getDate().getYear() == year) {
                if (isIncome) monthlyIncome = monthlyIncome.add(amt);
                else monthlyExpense = monthlyExpense.add(amt);
            }
        }
        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(totalIncome.subtract(totalExpense))
                .categoryIncome(toCategoryList(incomeMap))
                .categoryExpense(toCategoryList(expenseMap))
                .monthlyIncome(monthlyIncome)
                .monthlyExpense(monthlyExpense)
                .recentTransactions(getRecent(all))
                .transactionCount((long) all.size())
                .build();
    }

    private AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new InvalidCredentialsException("Unauthorized");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private BigDecimal safe(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    private List<CategorySummary> mapToCategory(List<Object[]> data) {
        return data.stream()
                .map(obj -> new CategorySummary((String) obj[0], (BigDecimal) obj[1]))
                .collect(Collectors.toList());
    }

    private List<CategorySummary> toCategoryList(Map<String, BigDecimal> map) {
        return map.entrySet()
                .stream()
                .map(e -> new CategorySummary(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private List<Transaction> getRecent(List<Transaction> list) {
        return list.stream()
                .filter(t -> t.getDate() != null)
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .limit(5)
                .toList();
    }
}

package com.wt.financial_dashboard.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wt.financial_dashboard.util.Category;
import com.wt.financial_dashboard.util.Type;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transactionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private AppUser user;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Type type;
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    @Column(name = "description")
    private String description;
    private LocalDateTime date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void create() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void update() {
        updatedAt = LocalDateTime.now();
    }
}

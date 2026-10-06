package com.wt.financial_dashboard.controllers;

import com.wt.financial_dashboard.dtos.DashboardResponse;
import com.wt.financial_dashboard.dtos.TransactionRequest;
import com.wt.financial_dashboard.models.Transaction;
import com.wt.financial_dashboard.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService service;

    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = service.createTransaction(request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestParam int month, @RequestParam int year) {
        DashboardResponse response = service.getDashboard(month, year);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
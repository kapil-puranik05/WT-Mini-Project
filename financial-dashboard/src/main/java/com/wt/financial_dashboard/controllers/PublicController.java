package com.wt.financial_dashboard.controllers;

import com.wt.financial_dashboard.dtos.LoginRequest;
import com.wt.financial_dashboard.dtos.SignupRequest;
import com.wt.financial_dashboard.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {
    private final UserService service;

    @GetMapping("/health-check")
    public ResponseEntity<?> healthCheck() {
        return new ResponseEntity<>("Server is running.", HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        System.out.println("Email: " + request.getEmail());
        System.out.println("Password: " + request.getPassword());
        return new ResponseEntity<>(service.signup(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return new ResponseEntity<>(service.login(request), HttpStatus.OK);
    }
}

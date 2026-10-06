package com.wt.financial_dashboard.services;

import com.wt.financial_dashboard.exceptions.InvalidCredentialsException;
import com.wt.financial_dashboard.exceptions.UserAlreadyExistsException;
import com.wt.financial_dashboard.dtos.LoginRequest;
import com.wt.financial_dashboard.dtos.LoginResponse;
import com.wt.financial_dashboard.dtos.SignupRequest;
import com.wt.financial_dashboard.dtos.SignupResponse;
import com.wt.financial_dashboard.dtos.UserSummaryResponse;
import com.wt.financial_dashboard.exceptions.UserNotFoundException;
import com.wt.financial_dashboard.models.AppUser;
import com.wt.financial_dashboard.repositories.UserRepository;
import com.wt.financial_dashboard.util.JwtUtil;
import com.wt.financial_dashboard.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtUtil util;

    public SignupResponse signup(SignupRequest request) {
        if(repository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with provided email already exists.");
        }
        AppUser user = new AppUser();
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user = repository.save(user);
        SignupResponse response = new SignupResponse();
        response.setUserId(user.getUserId());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public LoginResponse login(LoginRequest request) {
        AppUser user = repository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User with provided email was not found"));
        if(!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        String token = util.generateToken(user.getEmail(), user.getRole().name());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return response;
    }

    public List<UserSummaryResponse> getAllUsers() {
        AppUser currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new InvalidCredentialsException("Only ADMIN can view all users");
        }

        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(AppUser::getEmail, String.CASE_INSENSITIVE_ORDER))
                .map(user -> new UserSummaryResponse(user.getUserId(), user.getEmail(), user.getRole()))
                .toList();
    }

    private AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new InvalidCredentialsException("Unauthorized");
        }

        return repository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User with provided email was not found"));
    }
}

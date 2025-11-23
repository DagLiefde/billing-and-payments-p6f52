package com.fabrica.p6f5.springapp.controller;

import com.fabrica.p6f5.springapp.dto.ApiResponse;
import com.fabrica.p6f5.springapp.dto.AuthResponse;
import com.fabrica.p6f5.springapp.dto.LoginRequest;
import com.fabrica.p6f5.springapp.dto.RegisterRequest;
import com.fabrica.p6f5.springapp.entity.User;
import com.fabrica.p6f5.springapp.service.AuthService;
import jakarta.validation.Valid;
import com.fabrica.p6f5.springapp.util.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Authentication Controller following Open/Closed Principle.
 * This controller is open for extension but closed for modification.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Register a new user.
     * 
     * @param request the registration request
     * @return ResponseEntity with AuthResponse
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseUtils.created(response, "User registered successfully");
    }
    
    /**
     * Authenticate user and return JWT token.
     * 
     * @param request the login request
     * @return ResponseEntity with AuthResponse
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseUtils.success(response, "Login successful");
    }
    
    /**
     * Get current user profile.
     * 
     * @return ResponseEntity with current user details
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile() {
        Optional<User> currentUser = authService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseUtils.errorTyped("User not authenticated", HttpStatus.UNAUTHORIZED);
        }
        return ResponseUtils.success(currentUser.get(), "Profile retrieved successfully");
    }
    
    /**
     * Validate JWT token.
     * 
     * @param token the JWT token
     * @return ResponseEntity with validation result
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseUtils.success(isValid, "Token validation completed");
    }
}

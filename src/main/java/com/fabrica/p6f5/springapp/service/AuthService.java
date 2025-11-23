package com.fabrica.p6f5.springapp.service;

import com.fabrica.p6f5.springapp.dto.AuthResponse;
import com.fabrica.p6f5.springapp.dto.LoginRequest;
import com.fabrica.p6f5.springapp.dto.RegisterRequest;
import com.fabrica.p6f5.springapp.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Authentication Service following Single Responsibility Principle.
 * This service is responsible only for authentication operations.
 */
@Service
public class AuthService {
    
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthService(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    
    /**
     * Register a new user.
     * 
     * @param request the registration request
     * @return AuthResponse with token and user details
     * @throws RuntimeException if user already exists
     */
    public AuthResponse register(RegisterRequest request) {
        validateUserDoesNotExist(request);
        User savedUser = createAndSaveUser(request);
        String token = jwtService.generateToken(savedUser);
        return createAuthResponse(token, savedUser);
    }
    
    /**
     * Validate that user does not already exist.
     */
    private void validateUserDoesNotExist(RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        if (userService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
    }
    
    /**
     * Create and save user from request.
     */
    private User createAndSaveUser(RegisterRequest request) {
        User user = request.toUser();
        return userService.save(user);
    }
    
    /**
     * Create authentication response.
     */
    private AuthResponse createAuthResponse(String token, User user) {
        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getId()
        );
    }
    
    /**
     * Authenticate user and return JWT token.
     * 
     * @param request the login request
     * @return AuthResponse with token and user details
     * @throws RuntimeException if authentication fails
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticateUser(request);
        User user = extractUserFromAuthentication(authentication);
        String token = jwtService.generateToken(user);
        return createAuthResponse(token, user);
    }
    
    /**
     * Authenticate user credentials.
     */
    private Authentication authenticateUser(LoginRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );
    }
    
    /**
     * Extract user from authentication.
     */
    private User extractUserFromAuthentication(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
    
    /**
     * Get current authenticated user.
     * 
     * @return Optional containing the current user
     */
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return Optional.of((User) authentication.getPrincipal());
        }
        return Optional.empty();
    }
    
    /**
     * Validate JWT token.
     * 
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            if (username == null) {
                return false;
            }
            UserDetails userDetails = userService.loadUserByUsername(username);
            return jwtService.isTokenValid(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }
}

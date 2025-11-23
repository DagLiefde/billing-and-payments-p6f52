package com.fabrica.p6f5.springapp.dto;

/**
 * DTO for authentication responses.
 * Follows Single Responsibility Principle by handling only authentication response data.
 */
public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    // Role field removed to match database schema
    private Long userId;
    
    // Default constructor
    public AuthResponse() {}
    
    // Constructor with parameters
    public AuthResponse(String token, String username, String email, Long userId) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.userId = userId;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Role getter and setter removed to match database schema
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

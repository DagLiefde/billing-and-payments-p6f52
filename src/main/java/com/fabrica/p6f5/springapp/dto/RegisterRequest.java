package com.fabrica.p6f5.springapp.dto;

import com.fabrica.p6f5.springapp.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration requests.
 * Follows Single Responsibility Principle by handling only registration request data.
 */
public class RegisterRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    // Default constructor
    public RegisterRequest() {}
    
    // Constructor with parameters
    public RegisterRequest(String username, String email, String fullName, String password) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
    }
    
    // Getters and Setters
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
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Convert RegisterRequest to User entity.
     * 
     * @return User entity with the data from this request
     */
    public User toUser() {
        User user = new User(username, email, password);
        user.setFullName(fullName);
        return user;
    }
}

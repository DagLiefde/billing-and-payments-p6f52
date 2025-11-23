package com.fabrica.p6f5.springapp.controller;

import com.fabrica.p6f5.springapp.dto.ApiResponse;
import com.fabrica.p6f5.springapp.entity.User;
import com.fabrica.p6f5.springapp.exception.ResourceNotFoundException;
import com.fabrica.p6f5.springapp.service.UserService;
import com.fabrica.p6f5.springapp.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller following Open/Closed Principle.
 * This controller is open for extension but closed for modification.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Get all users (Admin only).
     * 
     * @return ResponseEntity with list of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseUtils.success(users, "Users retrieved successfully");
    }
    
    /**
     * Get user by ID.
     * 
     * @param id the user ID
     * @return ResponseEntity with user details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return ResponseUtils.success(user, "User retrieved successfully");
    }
    
    /**
     * Update user (Admin only or own profile).
     * 
     * @param id the user ID
     * @param user the updated user data
     * @return ResponseEntity with updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.findById(#id).get().username == authentication.name")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        userService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        User updatedUser = userService.save(user);
        return ResponseUtils.success(updatedUser, "User updated successfully");
    }
    
    /**
     * Delete user (Admin only).
     * 
     * @param id the user ID
     * @return ResponseEntity with deletion result
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userService.deleteById(id);
        return ResponseUtils.success("User deleted successfully", "User deleted successfully");
    }
}

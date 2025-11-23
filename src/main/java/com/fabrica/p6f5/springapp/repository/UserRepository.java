package com.fabrica.p6f5.springapp.repository;

import com.fabrica.p6f5.springapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository interface following Dependency Inversion Principle.
 * This interface defines the contract for user data access operations.
 * High-level modules depend on this abstraction, not on concrete implementations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by username.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email.
     * 
     * @param email the email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a user exists by username.
     * 
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if a user exists by email.
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find a user by username or email.
     * 
     * @param username the username to search for
     * @param email the email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
}

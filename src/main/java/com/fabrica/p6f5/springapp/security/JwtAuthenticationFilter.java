package com.fabrica.p6f5.springapp.security;

import com.fabrica.p6f5.springapp.service.JwtService;
import com.fabrica.p6f5.springapp.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import com.fabrica.p6f5.springapp.util.Constants;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter following Single Responsibility Principle.
 * This class is responsible only for JWT token validation and authentication.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserService userService;
    
    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader(Constants.AUTHORIZATION_HEADER);
        if (!isValidAuthHeader(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwt = extractJwtToken(authHeader);
        authenticateToken(jwt, request);
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Check if authorization header is valid.
     */
    private boolean isValidAuthHeader(String authHeader) {
        return authHeader != null && authHeader.startsWith(Constants.BEARER_PREFIX);
    }
    
    /**
     * Extract JWT token from authorization header.
     */
    private String extractJwtToken(String authHeader) {
        return authHeader.substring(Constants.BEARER_PREFIX_LENGTH);
    }
    
    /**
     * Authenticate JWT token and set security context.
     */
    private void authenticateToken(String jwt, HttpServletRequest request) {
        try {
            String username = jwtService.extractUsername(jwt);
            if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                return;
            }
            
            UserDetails userDetails = userService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                setAuthentication(userDetails, request);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication", e);
        }
    }
    
    /**
     * Set authentication in security context.
     */
    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}

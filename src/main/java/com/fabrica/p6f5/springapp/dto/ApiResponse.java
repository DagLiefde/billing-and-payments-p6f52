package com.fabrica.p6f5.springapp.dto;

/**
 * Generic API response DTO.
 * Follows Single Responsibility Principle by handling only generic API response data.
 */
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private long timestamp;
    
    // Default constructor
    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Constructor for success response
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Static factory methods for common responses
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
    
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

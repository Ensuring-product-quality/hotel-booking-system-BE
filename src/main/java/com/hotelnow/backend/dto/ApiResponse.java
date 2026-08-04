package com.hotelnow.backend.dto;

import java.time.OffsetDateTime;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private OffsetDateTime timestamp;
    private int status;
    private T data;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, OffsetDateTime timestamp, int status, T data) {
        this.success = success;
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.data = data;
    }

    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<T>();
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public static <T> ApiResponse<T> success(String message, T data, int status) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .status(status)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Operation successful", data, 200);
    }

    public static <T> ApiResponse<T> error(String message, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .status(status)
                .data(null)
                .build();
    }

    public static class ApiResponseBuilder<T> {
        private boolean success;
        private String message;
        private OffsetDateTime timestamp;
        private int status;
        private T data;

        public ApiResponseBuilder<T> success(boolean success) { this.success = success; return this; }
        public ApiResponseBuilder<T> message(String message) { this.message = message; return this; }
        public ApiResponseBuilder<T> timestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; return this; }
        public ApiResponseBuilder<T> status(int status) { this.status = status; return this; }
        public ApiResponseBuilder<T> data(T data) { this.data = data; return this; }

        public ApiResponse<T> build() {
            return new ApiResponse<T>(success, message, timestamp, status, data);
        }
    }
}

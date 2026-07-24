package com.hotelnow.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomUpdateDTO {
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotBlank(message = "Room type is required")
    private String type;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 20, message = "Capacity must not exceed 20")
    private Integer capacity;

    private String description;

    @NotBlank(message = "Status is required")
    private String status;
}

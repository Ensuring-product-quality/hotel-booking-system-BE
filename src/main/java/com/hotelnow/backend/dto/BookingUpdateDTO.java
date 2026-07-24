package com.hotelnow.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingUpdateDTO {
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "At least 1 guest is required")
    private Integer guests;

    private String status;
}

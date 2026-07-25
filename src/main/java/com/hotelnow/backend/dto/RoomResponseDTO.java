package com.hotelnow.backend.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponseDTO {
    private Long id;
    private Long hotelId;
    private String roomNumber;
    private String type;
    private BigDecimal price;
    private Integer capacity;
    private String description;
    private String status;
}

package com.hotelnow.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String description;
    private String status;
}

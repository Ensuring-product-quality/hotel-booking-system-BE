package com.hotelnow.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDetailDTO {
    private Long id;
    private Long hotelId;
    private String roomNumber;
    private String type;
    private BigDecimal price;
    private String description;
    private String status;
    private List<String> images; // List of image URLs
}

package com.hotelnow.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDetailDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Integer stars;
    private String description;
    private String status;
    private Double averageRating;
    private List<String> images; // List of image URLs
    private List<RoomResponseDTO> rooms;
}

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
public class UserDetailDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String status;
    private String avatarUrl;
    private List<BookingResponseDTO> bookings;
}

package com.hotelnow.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class RoomStatusUpdateDTO {
    @NotBlank(message = "Trạng thái phòng không được để trống")
    private String status;

    public RoomStatusUpdateDTO() {}

    public RoomStatusUpdateDTO(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

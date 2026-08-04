package com.hotelnow.backend.dto;

import java.util.List;

public class HotelDetailDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Integer stars;
    private String description;
    private String status;
    private Double averageRating;
    private Long managerId;
    private String managerName;
    private List<String> images;
    private List<RoomResponseDTO> rooms;

    public HotelDetailDTO() {}

    public HotelDetailDTO(Long id, String name, String address, String city, Integer stars, String description, String status, Double averageRating, Long managerId, String managerName, List<String> images, List<RoomResponseDTO> rooms) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.stars = stars;
        this.description = description;
        this.status = status;
        this.averageRating = averageRating;
        this.managerId = managerId;
        this.managerName = managerName;
        this.images = images;
        this.rooms = rooms;
    }

    public static HotelDetailDTOBuilder builder() {
        return new HotelDetailDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public List<RoomResponseDTO> getRooms() { return rooms; }
    public void setRooms(List<RoomResponseDTO> rooms) { this.rooms = rooms; }

    public static class HotelDetailDTOBuilder {
        private Long id;
        private String name;
        private String address;
        private String city;
        private Integer stars;
        private String description;
        private String status;
        private Double averageRating;
        private Long managerId;
        private String managerName;
        private List<String> images;
        private List<RoomResponseDTO> rooms;

        public HotelDetailDTOBuilder id(Long id) { this.id = id; return this; }
        public HotelDetailDTOBuilder name(String name) { this.name = name; return this; }
        public HotelDetailDTOBuilder address(String address) { this.address = address; return this; }
        public HotelDetailDTOBuilder city(String city) { this.city = city; return this; }
        public HotelDetailDTOBuilder stars(Integer stars) { this.stars = stars; return this; }
        public HotelDetailDTOBuilder description(String description) { this.description = description; return this; }
        public HotelDetailDTOBuilder status(String status) { this.status = status; return this; }
        public HotelDetailDTOBuilder averageRating(Double averageRating) { this.averageRating = averageRating; return this; }
        public HotelDetailDTOBuilder managerId(Long managerId) { this.managerId = managerId; return this; }
        public HotelDetailDTOBuilder managerName(String managerName) { this.managerName = managerName; return this; }
        public HotelDetailDTOBuilder images(List<String> images) { this.images = images; return this; }
        public HotelDetailDTOBuilder rooms(List<RoomResponseDTO> rooms) { this.rooms = rooms; return this; }

        public HotelDetailDTO build() {
            return new HotelDetailDTO(id, name, address, city, stars, description, status, averageRating, managerId, managerName, images, rooms);
        }
    }
}

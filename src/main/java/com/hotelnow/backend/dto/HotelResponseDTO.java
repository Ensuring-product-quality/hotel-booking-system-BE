package com.hotelnow.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class HotelResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Integer stars;
    private String description;
    private String status;
    private Double averageRating;
    private BigDecimal price;
    private Long managerId;
    private String managerName;
    private List<String> images;

    public HotelResponseDTO() {}

    public HotelResponseDTO(Long id, String name, String address, String city, Integer stars, String description, String status, Double averageRating, BigDecimal price, Long managerId, String managerName, List<String> images) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.stars = stars;
        this.description = description;
        this.status = status;
        this.averageRating = averageRating;
        this.price = price;
        this.managerId = managerId;
        this.managerName = managerName;
        this.images = images;
    }

    public static HotelResponseDTOBuilder builder() {
        return new HotelResponseDTOBuilder();
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

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public static class HotelResponseDTOBuilder {
        private Long id;
        private String name;
        private String address;
        private String city;
        private Integer stars;
        private String description;
        private String status;
        private Double averageRating;
        private BigDecimal price;
        private Long managerId;
        private String managerName;
        private List<String> images;

        public HotelResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public HotelResponseDTOBuilder name(String name) { this.name = name; return this; }
        public HotelResponseDTOBuilder address(String address) { this.address = address; return this; }
        public HotelResponseDTOBuilder city(String city) { this.city = city; return this; }
        public HotelResponseDTOBuilder stars(Integer stars) { this.stars = stars; return this; }
        public HotelResponseDTOBuilder description(String description) { this.description = description; return this; }
        public HotelResponseDTOBuilder status(String status) { this.status = status; return this; }
        public HotelResponseDTOBuilder averageRating(Double averageRating) { this.averageRating = averageRating; return this; }
        public HotelResponseDTOBuilder price(BigDecimal price) { this.price = price; return this; }
        public HotelResponseDTOBuilder managerId(Long managerId) { this.managerId = managerId; return this; }
        public HotelResponseDTOBuilder managerName(String managerName) { this.managerName = managerName; return this; }
        public HotelResponseDTOBuilder images(List<String> images) { this.images = images; return this; }

        public HotelResponseDTO build() {
            return new HotelResponseDTO(id, name, address, city, stars, description, status, averageRating, price, managerId, managerName, images);
        }
    }
}

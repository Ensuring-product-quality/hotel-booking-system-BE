package com.hotelnow.backend.config;

import com.hotelnow.backend.entity.*;
import com.hotelnow.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           HotelRepository hotelRepository,
                           RoomRepository roomRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Data already initialized
        }

        // 1. Create Default Users
        User customer = User.builder()
                .username("customer")
                .password(passwordEncoder.encode("password"))
                .email("customer@hotelnow.com")
                .role(Role.CUSTOMER)
                .status("active")
                .build();

        User manager = User.builder()
                .username("manager")
                .password(passwordEncoder.encode("password"))
                .email("manager@hotelnow.com")
                .role(Role.MANAGER)
                .status("active")
                .build();

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@hotelnow.com")
                .role(Role.ADMIN)
                .status("active")
                .build();

        userRepository.saveAll(Arrays.asList(customer, manager, admin));

        // 2. Create Default Hotels
        Hotel h1 = Hotel.builder()
                .name("InterContinental Hanoi Westlake")
                .address("5 Từ Hoa, Tây Hồ")
                .city("Hanoi")
                .stars(5)
                .description("Sang trọng bậc nhất bên Hồ Tây thơ mộng.")
                .status("active")
                .averageRating(4.8)
                .build();

        Hotel h2 = Hotel.builder()
                .name("Sofitel Saigon Plaza")
                .address("17 Lê Duẩn, Quận 1")
                .city("Ho Chi Minh")
                .stars(5)
                .description("Khách sạn 5 sao mang phong cách Pháp lãng mạn giữa lòng Sài Gòn.")
                .status("active")
                .averageRating(4.6)
                .build();

        Hotel h3 = Hotel.builder()
                .name("Pullman Danang Beach Resort")
                .address("101 Võ Nguyên Giáp, Ngũ Hành Sơn")
                .city("Da Nang")
                .stars(5)
                .description("Khu nghỉ dưỡng sang trọng bên bãi biển Mỹ Khê.")
                .status("active")
                .averageRating(4.7)
                .build();

        hotelRepository.saveAll(Arrays.asList(h1, h2, h3));

        // 3. Create Rooms for InterContinental Hanoi Westlake
        Room r1 = Room.builder()
                .hotel(h1)
                .roomNumber("101")
                .type(RoomType.SINGLE)
                .price(BigDecimal.valueOf(1500000.00))
                .capacity(1) // 1.5M VND
                .description("Phòng đơn ấm cúng đầy đủ tiện nghi, view sân vườn.")
                .status("active")
                .build();

        Room r2 = Room.builder()
                .hotel(h1)
                .roomNumber("102")
                .type(RoomType.DOUBLE)
                .price(BigDecimal.valueOf(2500000.00))
                .capacity(2) // 2.5M VND
                .description("Phòng đôi rộng rãi thích hợp cho gia đình hoặc cặp đôi, hướng Hồ Tây.")
                .status("active")
                .build();

        Room r3 = Room.builder()
                .hotel(h1)
                .roomNumber("201")
                .type(RoomType.DELUXE)
                .price(BigDecimal.valueOf(4000000.00))
                .capacity(3) // 4M VND
                .description("Phòng Deluxe sang trọng với ban công riêng ngắm hoàng hôn Hồ Tây.")
                .status("active")
                .build();

        // Rooms for Sofitel Saigon Plaza
        Room r4 = Room.builder()
                .hotel(h2)
                .roomNumber("301")
                .type(RoomType.SINGLE)
                .price(BigDecimal.valueOf(1800000.00))
                .capacity(1)
                .description("Phòng đơn thiết kế tinh tế tinh xảo kiểu Pháp.")
                .status("active")
                .build();

        Room r5 = Room.builder()
                .hotel(h2)
                .roomNumber("302")
                .type(RoomType.SUITE)
                .price(BigDecimal.valueOf(5500000.00))
                .capacity(4)
                .description("Phòng Suite tổng thống siêu sang trọng view toàn cảnh Sài Gòn.")
                .status("active")
                .build();

        // Rooms for Pullman Danang Beach Resort
        Room r6 = Room.builder()
                .hotel(h3)
                .roomNumber("401")
                .type(RoomType.DOUBLE)
                .price(BigDecimal.valueOf(3000000.00))
                .capacity(2)
                .description("Phòng đôi hướng biển, nghe sóng vỗ rì rào.")
                .status("active")
                .build();

        Room r7 = Room.builder()
                .hotel(h3)
                .roomNumber("402")
                .type(RoomType.DELUXE)
                .price(BigDecimal.valueOf(4800000.00))
                .capacity(3)
                .description("Phòng Deluxe sát biển cực kỳ riêng tư và lãng mạn.")
                .status("active")
                .build();

        roomRepository.saveAll(Arrays.asList(r1, r2, r3, r4, r5, r6, r7));
    }
}

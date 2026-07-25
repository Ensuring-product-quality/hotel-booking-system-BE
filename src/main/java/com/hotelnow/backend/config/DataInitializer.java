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
                .city("Hà Nội")
                .stars(5)
                .description("Sang trọng bậc nhất bên Hồ Tây thơ mộng.")
                .status("active")
                .averageRating(4.8)
                .build();

        Hotel h2 = Hotel.builder()
                .name("Sofitel Legend Metropole Hanoi")
                .address("15 Ngô Quyền, Hoàn Kiếm")
                .city("Hà Nội")
                .stars(5)
                .description("Khách sạn cổ kính mang phong cách Pháp thuộc địa đầy nghệ thuật.")
                .status("active")
                .averageRating(4.9)
                .build();

        Hotel h3 = Hotel.builder()
                .name("Sofitel Saigon Plaza")
                .address("17 Lê Duẩn, Quận 1")
                .city("Hồ Chí Minh")
                .stars(5)
                .description("Khách sạn 5 sao mang phong cách Pháp lãng mạn giữa lòng Sài Gòn.")
                .status("active")
                .averageRating(4.6)
                .build();

        Hotel h4 = Hotel.builder()
                .name("The Reverie Saigon")
                .address("22-36 Nguyễn Huệ, Quận 1")
                .city("Hồ Chí Minh")
                .stars(5)
                .description("Thiết kế vương giả Ý, biểu tượng của sự xa hoa hoa lệ.")
                .status("active")
                .averageRating(4.9)
                .build();

        Hotel h5 = Hotel.builder()
                .name("Pullman Danang Beach Resort")
                .address("101 Võ Nguyên Giáp, Ngũ Hành Sơn")
                .city("Đà Nẵng")
                .stars(5)
                .description("Khu nghỉ dưỡng sang trọng bên bãi biển Mỹ Khê.")
                .status("active")
                .averageRating(4.7)
                .build();

        Hotel h6 = Hotel.builder()
                .name("InterContinental Danang Sun Peninsula Resort")
                .address("Bán đảo Sơn Trà, Ngũ Hành Sơn")
                .city("Đà Nẵng")
                .stars(5)
                .description("Thiên đường nghỉ dưỡng ẩn mình giữa thiên nhiên hoang sơ bán đảo Sơn Trà.")
                .status("active")
                .averageRating(4.9)
                .build();

        Hotel h7 = Hotel.builder()
                .name("Little Riverside Hoi An Luxury Hotel & Spa")
                .address("09 Phan Bội Châu, Cẩm Châu")
                .city("Hội An")
                .stars(5)
                .description("Khách sạn boutique yên bình nép mình bên dòng sông Thu Bồn thơ mộng.")
                .status("active")
                .averageRating(4.8)
                .build();

        Hotel h8 = Hotel.builder()
                .name("Vinpearl Resort & Spa Ha Long")
                .address("Đảo Rều, Bãi Cháy")
                .city("Hạ Long")
                .stars(5)
                .description("Lâu đài tráng lệ nổi bật giữa lòng kỳ quan thiên nhiên thế giới Vịnh Hạ Long.")
                .status("active")
                .averageRating(4.7)
                .build();

        Hotel h9 = Hotel.builder()
                .name("Dalat Palace Heritage Hotel")
                .address("02 Trần Phú, Phường 3")
                .city("Đà Lạt")
                .stars(5)
                .description("Biệt thự Pháp cổ kính, ngắm trọn vẹn cảnh Hồ Xuân Hương thơ mộng.")
                .status("active")
                .averageRating(4.6)
                .build();

        Hotel h10 = Hotel.builder()
                .name("Hotel de la Coupole - MGallery")
                .address("01 Hoàng Liên, Sa Pa")
                .city("Sa Pa")
                .stars(5)
                .description("Kiệt tác kiến trúc Haute Couture kết hợp nét đẹp văn hóa Tây Bắc.")
                .status("active")
                .averageRating(4.8)
                .build();

        Hotel h11 = Hotel.builder()
                .name("Regent Phu Quoc")
                .address("Bãi Trường, Dương Tơ")
                .city("Phú Quốc")
                .stars(5)
                .description("Khách sạn nghỉ dưỡng siêu sang trọng mang lại trải nghiệm cá nhân hóa đỉnh cao.")
                .status("active")
                .averageRating(4.9)
                .build();

        hotelRepository.saveAll(Arrays.asList(h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11));

        // 3. Create Rooms
        // Rooms for InterContinental Hanoi Westlake
        Room r1 = Room.builder().hotel(h1).roomNumber("101").type(RoomType.SINGLE).price(BigDecimal.valueOf(1500000.00)).description("Phòng đơn ấm cúng đầy đủ tiện nghi, view sân vườn.").status("active").build();
        Room r2 = Room.builder().hotel(h1).roomNumber("102").type(RoomType.DOUBLE).price(BigDecimal.valueOf(2500000.00)).description("Phòng đôi rộng rãi thích hợp cho gia đình hoặc cặp đôi, hướng Hồ Tây.").status("active").build();
        Room r3 = Room.builder().hotel(h1).roomNumber("201").type(RoomType.DELUXE).price(BigDecimal.valueOf(4000000.00)).description("Phòng Deluxe sang trọng với ban công riêng ngắm hoàng hôn Hồ Tây.").status("active").build();

        // Rooms for Sofitel Legend Metropole Hanoi
        Room r4 = Room.builder().hotel(h2).roomNumber("501").type(RoomType.SUITE).price(BigDecimal.valueOf(8000000.00)).description("Phòng Suite hoàng gia phong cách cổ điển Pháp.").status("active").build();
        Room r5 = Room.builder().hotel(h2).roomNumber("502").type(RoomType.DELUXE).price(BigDecimal.valueOf(5000000.00)).description("Phòng Deluxe lịch lãm ngắm phố cổ Hà Nội.").status("active").build();

        // Rooms for Sofitel Saigon Plaza
        Room r6 = Room.builder().hotel(h3).roomNumber("301").type(RoomType.SINGLE).price(BigDecimal.valueOf(1800000.00)).description("Phòng đơn thiết kế tinh tế tinh xảo kiểu Pháp.").status("active").build();
        Room r7 = Room.builder().hotel(h3).roomNumber("302").type(RoomType.SUITE).price(BigDecimal.valueOf(5500000.00)).description("Phòng Suite tổng thống siêu sang trọng view toàn cảnh Sài Gòn.").status("active").build();

        // Rooms for The Reverie Saigon
        Room r8 = Room.builder().hotel(h4).roomNumber("601").type(RoomType.DELUXE).price(BigDecimal.valueOf(6500000.00)).description("Phòng Deluxe hoàng gia Ý view sông Sài Gòn cực đẹp.").status("active").build();
        Room r9 = Room.builder().hotel(h4).roomNumber("602").type(RoomType.SUITE).price(BigDecimal.valueOf(12000000.00)).description("Phòng Suite siêu sang trọng bậc nhất Sài Thành.").status("active").build();

        // Rooms for Pullman Danang Beach Resort
        Room r10 = Room.builder().hotel(h5).roomNumber("401").type(RoomType.DOUBLE).price(BigDecimal.valueOf(3000000.00)).description("Phòng đôi hướng biển, nghe sóng vỗ rì rào.").status("active").build();
        Room r11 = Room.builder().hotel(h5).roomNumber("402").type(RoomType.DELUXE).price(BigDecimal.valueOf(4800000.00)).description("Phòng Deluxe sát biển cực kỳ riêng tư và lãng mạn.").status("active").build();

        // Rooms for InterContinental Danang Sun Peninsula Resort
        Room r12 = Room.builder().hotel(h6).roomNumber("701").type(RoomType.SUITE).price(BigDecimal.valueOf(9500000.00)).description("Phòng biệt thự Suite trên sườn đồi ngắm trọn vịnh biển Sơn Trà.").status("active").build();
        Room r13 = Room.builder().hotel(h6).roomNumber("702").type(RoomType.DELUXE).price(BigDecimal.valueOf(6500000.00)).description("Phòng Deluxe phong cách mộc mạc đẳng cấp.").status("active").build();

        // Rooms for Little Riverside Hoi An Luxury Hotel & Spa
        Room r14 = Room.builder().hotel(h7).roomNumber("201").type(RoomType.DOUBLE).price(BigDecimal.valueOf(2200000.00)).description("Phòng đôi ấm cúng view sông Hoài thơ mộng, phố cổ Hội An.").status("active").build();
        Room r15 = Room.builder().hotel(h7).roomNumber("202").type(RoomType.DELUXE).price(BigDecimal.valueOf(3200000.00)).description("Phòng Deluxe thiết kế Indochine tinh xảo.").status("active").build();

        // Rooms for Vinpearl Resort & Spa Ha Long
        Room r16 = Room.builder().hotel(h8).roomNumber("301").type(RoomType.DOUBLE).price(BigDecimal.valueOf(2800000.00)).description("Phòng Double hướng biển, ngắm trọn vẹn vịnh Hạ Long kỳ vĩ.").status("active").build();
        Room r17 = Room.builder().hotel(h8).roomNumber("302").type(RoomType.DELUXE).price(BigDecimal.valueOf(4200000.00)).description("Phòng Deluxe cao cấp với dịch vụ phục vụ tại phòng 24/7.").status("active").build();

        // Rooms for Dalat Palace Heritage Hotel
        Room r18 = Room.builder().hotel(h9).roomNumber("111").type(RoomType.SINGLE).price(BigDecimal.valueOf(1600000.00)).description("Phòng đơn ấm áp bên sườn đồi thông Đà Lạt.").status("active").build();
        Room r19 = Room.builder().hotel(h9).roomNumber("112").type(RoomType.DOUBLE).price(BigDecimal.valueOf(2600000.00)).description("Phòng đôi cổ điển hướng ra Hồ Xuân Hương.").status("active").build();
        Room r20 = Room.builder().hotel(h9).roomNumber("113").type(RoomType.DELUXE).price(BigDecimal.valueOf(3800000.00)).description("Phòng Deluxe sang trọng đậm chất quý tộc Pháp cổ.").status("active").build();

        // Rooms for Hotel de la Coupole - MGallery
        Room r21 = Room.builder().hotel(h10).roomNumber("501").type(RoomType.DOUBLE).price(BigDecimal.valueOf(3500000.00)).description("Phòng đôi phong cách Đông Dương giao thoa thời trang Haute Couture Pháp.").status("active").build();
        Room r22 = Room.builder().hotel(h10).roomNumber("502").type(RoomType.SUITE).price(BigDecimal.valueOf(7000000.00)).description("Phòng Suite hoàng gia hướng ra thung lũng Mường Hoa đầy sương mù.").status("active").build();

        // Rooms for Regent Phu Quoc
        Room r23 = Room.builder().hotel(h11).roomNumber("901").type(RoomType.SUITE).price(BigDecimal.valueOf(11000000.00)).description("Phòng Suite hồ bơi vô cực riêng ngắm hoàng hôn đảo ngọc Phú Quốc.").status("active").build();
        Room r24 = Room.builder().hotel(h11).roomNumber("902").type(RoomType.DELUXE).price(BigDecimal.valueOf(7500000.00)).description("Phòng Deluxe sang trọng bật nhất với tiện ích thông minh.").status("active").build();

        roomRepository.saveAll(Arrays.asList(
                r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24
        ));
    }
}

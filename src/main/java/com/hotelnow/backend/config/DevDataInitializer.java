package com.hotelnow.backend.config;

import com.hotelnow.backend.entity.*;
import com.hotelnow.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Arrays;

@Component
@ConditionalOnProperty(name = "app.seed-data.enabled", havingValue = "true", matchIfMissing = true)
public class DevDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final PasswordEncoder passwordEncoder;

    // High quality room images from Unsplash matching room styles
    private static final String[] SINGLE_IMAGES = {
            "https://images.unsplash.com/photo-1505691938895-1758d7feb511?q=80&w=600",
            "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?q=80&w=600"
    };

    private static final String[] DOUBLE_IMAGES = {
            "https://images.unsplash.com/photo-1590490360182-c33d57733427?q=80&w=600",
            "https://images.unsplash.com/photo-1566665797739-1674de7a421a?q=80&w=600"
    };

    private static final String[] DELUXE_IMAGES = {
            "https://images.unsplash.com/photo-1618773928121-c32242e63f39?q=80&w=600",
            "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?q=80&w=600"
    };

    private static final String[] SUITE_IMAGES = {
            "https://images.unsplash.com/photo-1591088398332-8a7791972843?q=80&w=600",
            "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?q=80&w=600"
    };

    public DevDataInitializer(UserRepository userRepository,
                              HotelRepository hotelRepository,
                              RoomRepository roomRepository,
                              RoomImageRepository roomImageRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.roomImageRepository = roomImageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ensure default staff user exists
        if (!userRepository.existsByUsername("staff")) {
            User staff = User.builder()
                    .username("staff")
                    .password(passwordEncoder.encode("password"))
                    .email("staff@hotelnow.com")
                    .fullName("Phạm Lễ Tân")
                    .phone("0933445566")
                    .role(Role.STAFF)
                    .status("active")
                    .build();
            userRepository.save(staff);
        }

        if (userRepository.count() > 0 && hotelRepository.count() > 0) {
            return; // Data already initialized
        }

        // 1. Create Default Users if missing
        if (!userRepository.existsByUsername("customer")) {
            User customer = User.builder()
                    .username("customer")
                    .password(passwordEncoder.encode("password"))
                    .email("customer@hotelnow.com")
                    .fullName("Nguyễn Văn An")
                    .phone("0912345678")
                    .role(Role.CUSTOMER)
                    .status("active")
                    .build();
            userRepository.save(customer);
        }

        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("password"))
                    .email("admin@hotelnow.com")
                    .fullName("Lê Quản Trị")
                    .phone("0909090909")
                    .role(Role.ADMIN)
                    .status("active")
                    .build();
            userRepository.save(admin);
        }

        // 2. Create Default Hotels representing all 12 destinations on UI
        // MIỀN BẮC
        Hotel h1 = Hotel.builder()
                .name("InterContinental Hanoi Westlake")
                .address("5 Từ Hoa, Tây Hồ")
                .city("Hà Nội")
                .stars(5)
                .description("Sang trọng bậc nhất bên Hồ Tây thơ mộng.")
                .status("active")
                .averageRating(4.8)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600",
                        "https://images.unsplash.com/photo-1576013551627-0cc20b96c2a7?q=80&w=600"
                ))
                .build();

        Hotel h2 = Hotel.builder()
                .name("Vinpearl Resort & Spa Ha Long")
                .address("Đảo Rều, Bãi Cháy")
                .city("Hạ Long")
                .stars(5)
                .description("Lâu đài tráng lệ nổi bật giữa kỳ quan thiên nhiên Vịnh Hạ Long.")
                .status("active")
                .averageRating(4.7)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600",
                        "https://images.unsplash.com/photo-1576013551627-0cc20b96c2a7?q=80&w=600"
                ))
                .build();

        Hotel h3 = Hotel.builder()
                .name("Hotel de la Coupole - MGallery")
                .address("01 Hoàng Liên, Sa Pa")
                .city("Sa Pa")
                .stars(5)
                .description("Thiết kế Pháp lộng lẫy hòa quyện cùng bản sắc vùng cao Sa Pa.")
                .status("active")
                .averageRating(4.9)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600",
                        "https://images.unsplash.com/photo-1576013551627-0cc20b96c2a7?q=80&w=600"
                ))
                .build();

        Hotel h4 = Hotel.builder()
                .name("Emeralda Resort Ninh Binh")
                .address("Khu bảo tồn Vân Long, Gia Vân, Gia Viễn")
                .city("Ninh Bình")
                .stars(5)
                .description("Không gian thanh bình đậm chất làng quê Bắc Bộ xưa cổ kính.")
                .status("active")
                .averageRating(4.5)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1584132967334-10e028bd69f7?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600"
                ))
                .build();

        // MIỀN TRUNG
        Hotel h5 = Hotel.builder()
                .name("Pullman Danang Beach Resort")
                .address("101 Võ Nguyên Giáp, Ngũ Hành Sơn")
                .city("Đà Nẵng")
                .stars(5)
                .description("Khu nghỉ dưỡng sang trọng bên bãi biển Mỹ Khê cát trắng.")
                .status("active")
                .averageRating(4.7)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1571896349842-33c89424de2d?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600"
                ))
                .build();

        Hotel h6 = Hotel.builder()
                .name("Little Riverside Hoi An Luxury Hotel")
                .address("09 Phan Bội Châu, Cẩm Châu")
                .city("Hội An")
                .stars(5)
                .description("Boutique hotel thanh bình bên dòng sông Thu Bồn thơ mộng.")
                .status("active")
                .averageRating(4.8)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1529290130-4ca3753253ae?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600"
                ))
                .build();

        Hotel h7 = Hotel.builder()
                .name("Vinpearl Resort & Spa Nha Trang Bay")
                .address("Đảo Hòn Tre, Vĩnh Nguyên")
                .city("Nha Trang")
                .stars(5)
                .description("Khu resort xanh mát ôm trọn vịnh Nha Trang biển xanh nắng vàng.")
                .status("active")
                .averageRating(4.6)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600",
                        "https://images.unsplash.com/photo-1576013551627-0cc20b96c2a7?q=80&w=600"
                ))
                .build();

        Hotel h8 = Hotel.builder()
                .name("Dalat Palace Heritage Hotel")
                .address("02 Trần Phú, Phường 3")
                .city("Đà Lạt")
                .stars(5)
                .description("Dinh thự cổ điển đậm phong cách Pháp nhìn ra hồ Xuân Hương.")
                .status("active")
                .averageRating(4.6)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1445019980597-93fa8acb246c?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600"
                ))
                .build();

        // MIỀN NAM
        Hotel h9 = Hotel.builder()
                .name("Sofitel Saigon Plaza")
                .address("17 Lê Duẩn, Quận 1")
                .city("Hồ Chí Minh")
                .stars(5)
                .description("Giao thoa nét sang trọng Pháp và nét năng động sầm uất Sài Gòn.")
                .status("active")
                .averageRating(4.6)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600",
                        "https://images.unsplash.com/photo-1576013551627-0cc20b96c2a7?q=80&w=600"
                ))
                .build();

        Hotel h10 = Hotel.builder()
                .name("Regent Phu Quoc")
                .address("Bãi Trường, Dương Tơ")
                .city("Phú Quốc")
                .stars(5)
                .description("Thiên đường nghỉ dưỡng riêng tư siêu sang trọng ngắm hoàng hôn biển Phú Quốc.")
                .status("active")
                .averageRating(4.9)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600"
                ))
                .build();

        Hotel h11 = Hotel.builder()
                .name("The Imperial Hotel Vung Tau")
                .address("159 Thùy Vân, Thắng Tam")
                .city("Vũng Tàu")
                .stars(5)
                .description("Khách sạn phong cách hoàng gia Châu Âu cổ điển độc nhất bên bờ biển.")
                .status("active")
                .averageRating(4.5)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600"
                ))
                .build();

        Hotel h12 = Hotel.builder()
                .name("Azerai Can Tho")
                .address("Cồn Ấu, Cái Răng")
                .city("Cần Thơ")
                .stars(5)
                .description("Khu ốc đảo xanh bình yên nép mình bên dòng sông Hậu miền Tây.")
                .status("active")
                .averageRating(4.7)
                .imageUrl(String.join(",",
                        "https://images.unsplash.com/photo-1540555700478-4be289fbecef?q=80&w=600", // Main
                        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=600",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=600",
                        "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=600",
                        "https://images.unsplash.com/photo-1582719508461-905c673771fd?q=80&w=600",
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?q=80&w=600",
                        "https://images.unsplash.com/photo-1546548970-71785318a17b?q=80&w=600"
                ))
                .build();

        hotelRepository.saveAll(Arrays.asList(h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12));

        // 3. Create rooms with proper type, capacity, description, and high-quality Unsplash images
        // Hà Nội (h1)
        createRoomWithImages(h1, "101", RoomType.SINGLE, BigDecimal.valueOf(1500000.00), 1, "Phòng đơn ấm cúng đầy đủ tiện nghi, view sân vườn.", SINGLE_IMAGES);
        createRoomWithImages(h1, "102", RoomType.DOUBLE, BigDecimal.valueOf(2500000.00), 2, "Phòng đôi rộng rãi thích hợp cho gia đình hoặc cặp đôi, hướng Hồ Tây.", DOUBLE_IMAGES);

        // Hạ Long (h2)
        createRoomWithImages(h2, "201", RoomType.DOUBLE, BigDecimal.valueOf(2800000.00), 2, "Phòng Double hướng biển, ngắm trọn vẹn vịnh Hạ Long kỳ vĩ.", DOUBLE_IMAGES);
        createRoomWithImages(h2, "202", RoomType.DELUXE, BigDecimal.valueOf(4200000.00), 4, "Phòng Deluxe cao cấp với dịch vụ phục vụ tại phòng 24/7.", DELUXE_IMAGES);

        // Sa Pa (h3)
        createRoomWithImages(h3, "301", RoomType.DOUBLE, BigDecimal.valueOf(3500000.00), 2, "Phòng đôi phong cách Đông Dương giao thoa thời trang Haute Couture Pháp.", DOUBLE_IMAGES);
        createRoomWithImages(h3, "302", RoomType.SUITE, BigDecimal.valueOf(7000000.00), 6, "Phòng Suite hoàng gia hướng ra thung lũng Mường Hoa đầy sương mù.", SUITE_IMAGES);

        // Ninh Bình (h4)
        createRoomWithImages(h4, "401", RoomType.DOUBLE, BigDecimal.valueOf(2200000.00), 2, "Phòng đôi mộc mạc hướng ra núi đá vôi Tràng An hùng vĩ.", DOUBLE_IMAGES);
        createRoomWithImages(h4, "402", RoomType.DELUXE, BigDecimal.valueOf(3800000.00), 4, "Biệt thự Deluxe sân vườn rộng thoáng mát đậm phong cách Bắc Bộ.", DELUXE_IMAGES);

        // Đà Nẵng (h5)
        createRoomWithImages(h5, "501", RoomType.DOUBLE, BigDecimal.valueOf(3000000.00), 2, "Phòng đôi hướng biển, nghe sóng vỗ rì rào bãi cát trắng.", DOUBLE_IMAGES);
        createRoomWithImages(h5, "502", RoomType.DELUXE, BigDecimal.valueOf(4800000.00), 4, "Phòng Deluxe sát biển cực kỳ riêng tư và lãng mạn.", DELUXE_IMAGES);

        // Hội An (h6)
        createRoomWithImages(h6, "601", RoomType.DOUBLE, BigDecimal.valueOf(2200000.00), 2, "Phòng đôi ấm cúng view sông Hoài thơ mộng, phố cổ Hội An.", DOUBLE_IMAGES);
        createRoomWithImages(h6, "602", RoomType.DELUXE, BigDecimal.valueOf(3200000.00), 4, "Phòng Deluxe thiết kế Indochine tinh xảo, bồn tắm gỗ cổ điển.", DELUXE_IMAGES);

        // Nha Trang (h7)
        createRoomWithImages(h7, "701", RoomType.DOUBLE, BigDecimal.valueOf(3200000.00), 2, "Phòng đôi view trọn vẹn vịnh biển Nha Trang tuyệt đẹp.", DOUBLE_IMAGES);
        createRoomWithImages(h7, "702", RoomType.SUITE, BigDecimal.valueOf(6500000.00), 6, "Phòng Suite biệt thự hồ bơi riêng tư thích hợp gia đình nghỉ ngơi.", SUITE_IMAGES);

        // Đà Lạt (h8)
        createRoomWithImages(h8, "801", RoomType.SINGLE, BigDecimal.valueOf(1600000.00), 1, "Phòng đơn ấm áp bên sườn đồi thông Đà Lạt thơ mộng.", SINGLE_IMAGES);
        createRoomWithImages(h8, "802", RoomType.DOUBLE, BigDecimal.valueOf(2600000.00), 2, "Phòng đôi cổ điển Pháp hướng ra Hồ Xuân Hương mờ sương.", DOUBLE_IMAGES);

        // Hồ Chí Minh (h9)
        createRoomWithImages(h9, "901", RoomType.SINGLE, BigDecimal.valueOf(1800000.00), 1, "Phòng đơn thiết kế tinh tế tinh xảo kiểu Pháp giữa trung tâm.", SINGLE_IMAGES);
        createRoomWithImages(h9, "902", RoomType.SUITE, BigDecimal.valueOf(5500000.00), 6, "Phòng Suite tổng thống siêu sang trọng view toàn cảnh Sài Gòn.", SUITE_IMAGES);

        // Phú Quốc (h10)
        createRoomWithImages(h10, "1001", RoomType.DELUXE, BigDecimal.valueOf(7500000.00), 4, "Phòng Deluxe sang trọng bậc nhất với tiện ích thông minh.", DELUXE_IMAGES);
        createRoomWithImages(h10, "1002", RoomType.SUITE, BigDecimal.valueOf(11000000.00), 6, "Phòng Suite hồ bơi vô cực riêng ngắm hoàng hôn tuyệt đẹp.", SUITE_IMAGES);

        // Vũng Tàu (h11)
        createRoomWithImages(h11, "1101", RoomType.DOUBLE, BigDecimal.valueOf(2400000.00), 2, "Phòng Double hoàng gia ngắm biển Vũng Tàu sóng vỗ rì rào.", DOUBLE_IMAGES);
        createRoomWithImages(h11, "1102", RoomType.DELUXE, BigDecimal.valueOf(3900000.00), 4, "Phòng Deluxe thiết kế nội thất gỗ phong cách hoàng gia Châu Âu.", DELUXE_IMAGES);

        // Cần Thơ (h12)
        createRoomWithImages(h12, "1201", RoomType.DOUBLE, BigDecimal.valueOf(3600000.00), 2, "Phòng đôi bungalow bên rặng trúc, sông nước trong lành.", DOUBLE_IMAGES);
        createRoomWithImages(h12, "1202", RoomType.SUITE, BigDecimal.valueOf(7500000.00), 6, "Biệt thự Suite tổng thống độc lập view sông Hậu tuyệt đẹp.", SUITE_IMAGES);
    }

    private void createRoomWithImages(Hotel hotel, String roomNumber, RoomType type, BigDecimal price, int capacity, String description, String... imageUrls) {
        Room room = Room.builder()
                .hotel(hotel)
                .roomNumber(roomNumber)
                .type(type)
                .price(price)
                .capacity(capacity)
                .description(description)
                .status("active")
                .build();
        room = roomRepository.save(room);

        for (String url : imageUrls) {
            RoomImage img = RoomImage.builder()
                    .room(room)
                    .imageUrl(url)
                    .build();
            roomImageRepository.save(img);
        }
    }
}

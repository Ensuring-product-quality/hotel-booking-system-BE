package com.hotelnow.backend;

import com.hotelnow.backend.dto.BookingCreateDTO;
import com.hotelnow.backend.entity.*;
import com.hotelnow.backend.exception.BookingConflictException;
import com.hotelnow.backend.repository.*;
import com.hotelnow.backend.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private Room room;

    @BeforeEach
    public void setup() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();

        user = User.builder()
                .username("testcustomer")
                .password("password")
                .email("testcustomer@gmail.com")
                .role(Role.CUSTOMER)
                .status("active")
                .build();
        user = userRepository.save(user);

        Hotel hotel = Hotel.builder()
                .name("Grand Hotel")
                .address("123 Street")
                .city("Hanoi")
                .stars(5)
                .description("Luxury hotel")
                .status("active")
                .build();
        hotel = hotelRepository.save(hotel);

        room = Room.builder()
                .hotel(hotel)
                .roomNumber("101")
                .type(RoomType.DELUXE)
                .price(BigDecimal.valueOf(100.00))
                .description("Nice room")
                .status("active")
                .build();
        room = roomRepository.save(room);
    }

    @Test
    public void testConcurrentBookings() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);
        AtomicInteger otherErrors = new AtomicInteger(0);

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                latch.await();
                BookingCreateDTO createDTO = new BookingCreateDTO();
                createDTO.setUserId(user.getId());
                createDTO.setRoomId(room.getId());
                createDTO.setCheckInDate(LocalDate.now().plusDays(1));
                createDTO.setCheckOutDate(LocalDate.now().plusDays(3));
                createDTO.setGuests(2);

                try {
                    bookingService.createBooking(createDTO);
                    successCount.incrementAndGet();
                } catch (BookingConflictException e) {
                    conflictCount.incrementAndGet();
                } catch (Exception e) {
                    otherErrors.incrementAndGet();
                }
                return null;
            });
        }

        List<Future<Void>> futures = new ArrayList<>();
        for (Callable<Void> task : tasks) {
            futures.add(executorService.submit(task));
        }

        latch.countDown();

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(1, successCount.get(), "Exactly one booking should succeed");
        assertEquals(threadCount - 1, conflictCount.get(), "The rest should fail due to booking conflict");
        assertEquals(0, otherErrors.get(), "No other unexpected errors should occur");

        long bookingCount = bookingRepository.count();
        assertEquals(1, bookingCount, "Exactly one booking should be saved in database");
    }
}

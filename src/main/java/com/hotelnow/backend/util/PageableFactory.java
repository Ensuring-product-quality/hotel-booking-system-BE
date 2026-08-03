package com.hotelnow.backend.util;

import com.hotelnow.backend.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageableFactory {
    private PageableFactory() {
    }

    public static Pageable create(
            int page,
            int size,
            String sort,
            Set<String> allowedFields) {
        if (page < 0) {
            throw new BadRequestException("Trang hiển thị phải từ 0 trở lên");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("Số lượng phần tử trên trang phải từ 1 đến 100");
        }

        String[] parts = sort == null ? new String[0] : sort.split(",", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new BadRequestException("Cấu trúc sắp xếp phải theo định dạng trường,asc hoặc trường,desc");
        }

        String field = parts[0].trim();
        if (!allowedFields.contains(field)) {
            throw new BadRequestException("Trường sắp xếp không được hỗ trợ: " + field);
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(parts[1].trim());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Chiều sắp xếp phải là asc hoặc desc");
        }

        return PageRequest.of(page, size, Sort.by(direction, field));
    }
}

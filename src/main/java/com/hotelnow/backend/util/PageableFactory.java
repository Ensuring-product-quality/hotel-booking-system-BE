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
            throw new BadRequestException("Page must be zero or greater");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("Size must be between 1 and 100");
        }

        String[] parts = sort == null ? new String[0] : sort.split(",", -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new BadRequestException("Sort must use the format field,asc or field,desc");
        }

        String field = parts[0].trim();
        if (!allowedFields.contains(field)) {
            throw new BadRequestException("Unsupported sort field: " + field);
        }

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(parts[1].trim());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Sort direction must be asc or desc");
        }

        return PageRequest.of(page, size, Sort.by(direction, field));
    }
}

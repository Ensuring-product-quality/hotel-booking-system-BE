package com.hotelnow.backend.service;

import com.hotelnow.backend.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileStorageServiceTest {

    private static final byte[] PNG_1X1 = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=");
    private static final byte[] GIF_1X1 = Base64.getDecoder().decode(
            "R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==");

    @TempDir
    Path tempDirectory;

    private FileStorageService storage;

    @BeforeEach
    void setUp() {
        storage = new FileStorageService(tempDirectory.toString(), "http://localhost:8080/");
        storage.initialize();
    }

    @Test
    void storesAValidatedImageUnderGeneratedName() {
        MockMultipartFile file =
                new MockMultipartFile("file", "unsafe name.png", "image/png", PNG_1X1);

        String url = storage.storeImage(file, "hotels");

        assertTrue(url.matches("http://localhost:8080/uploads/hotels/[0-9a-f-]+\\.png"));
        String filename = url.substring(url.lastIndexOf('/') + 1);
        assertTrue(Files.exists(tempDirectory.resolve("hotels").resolve(filename)));
    }

    @Test
    void rejectsNonImageContent() {
        MockMultipartFile file =
                new MockMultipartFile("file", "fake.png", "image/png", "not an image".getBytes());

        BadRequestException error =
                assertThrows(BadRequestException.class, () -> storage.storeImage(file, "avatars"));

        assertEquals("Uploaded file is not a valid image", error.getMessage());
    }

    @Test
    void rejectsImageWhoseBytesDoNotMatchDeclaredType() {
        MockMultipartFile file =
                new MockMultipartFile("file", "wrong.png", "image/png", GIF_1X1);

        BadRequestException error =
                assertThrows(BadRequestException.class, () -> storage.storeImage(file, "avatars"));

        assertEquals("Image content does not match its content type", error.getMessage());
    }
}

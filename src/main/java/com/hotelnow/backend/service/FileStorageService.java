package com.hotelnow.backend.service;

import com.hotelnow.backend.exception.BadRequestException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/gif");
    private static final Map<String, String> CONTENT_TYPE_EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/gif", "gif");
    private static final Map<String, String> IMAGE_FORMATS = Map.of(
            "image/jpeg", "JPEG",
            "image/png", "PNG",
            "image/gif", "GIF");

    private final Path uploadRoot;
    private final String publicBaseUrl;

    public FileStorageService(
            @Value("${app.upload-dir:uploads}") String uploadDirectory,
            @Value("${app.base-url:http://localhost:8080}") String publicBaseUrl) {
        this.uploadRoot = Path.of(uploadDirectory).toAbsolutePath().normalize();
        this.publicBaseUrl = publicBaseUrl.replaceAll("/+$", "");
    }

    @PostConstruct
    void initialize() {
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not initialize upload directory", ex);
        }
    }

    public String storeImage(MultipartFile file, String category) {
        validateImage(file);
        String safeCategory = normalizeCategory(category);
        String extension = CONTENT_TYPE_EXTENSIONS.get(
                file.getContentType().toLowerCase(Locale.ROOT));
        String filename = UUID.randomUUID() + "." + extension;
        Path categoryDirectory = uploadRoot.resolve(safeCategory).normalize();
        Path destination = categoryDirectory.resolve(filename).normalize();

        if (!destination.startsWith(categoryDirectory)) {
            throw new BadRequestException("Invalid upload path");
        }

        try {
            Files.createDirectories(categoryDirectory);
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new BadRequestException("Could not save uploaded image");
        }

        return publicBaseUrl + "/uploads/" + safeCategory + "/" + filename;
    }

    public Path getUploadRoot() {
        return uploadRoot;
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is required");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BadRequestException("Image must not exceed 5 MB");
        }

        String contentType = file.getContentType();
        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BadRequestException("Only JPEG, PNG, or GIF images are allowed");
        }

        String expectedFormat = IMAGE_FORMATS.get(contentType.toLowerCase(Locale.ROOT));
        try (InputStream input = file.getInputStream();
             ImageInputStream imageInput = ImageIO.createImageInputStream(input)) {
            if (imageInput == null) {
                throw new BadRequestException("Uploaded file is not a valid image");
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);
            if (!readers.hasNext()) {
                throw new BadRequestException("Uploaded file is not a valid image");
            }
            ImageReader reader = readers.next();
            try {
                if (!reader.getFormatName().equalsIgnoreCase(expectedFormat)) {
                    throw new BadRequestException("Image content does not match its content type");
                }
                reader.setInput(imageInput, true, true);
                reader.read(0);
            } finally {
                reader.dispose();
            }
        } catch (IOException ex) {
            throw new BadRequestException("Could not read uploaded image");
        }
    }

    private String normalizeCategory(String category) {
        if (category == null || !category.matches("[a-z0-9-]+")) {
            throw new BadRequestException("Invalid upload category");
        }
        return category;
    }
}

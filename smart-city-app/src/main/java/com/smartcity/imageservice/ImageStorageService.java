package com.smartcity.imageservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path rootPath;

    public ImageStorageService(@Value("${app.storage.root}") String storageRoot) {
        this.rootPath = Path.of(storageRoot).toAbsolutePath().normalize();
    }

    /**
     * Saves an image to the file system organized by the current date (YYYY/MM/DD).
     *
     * @param inputStream      the image data stream
     * @param originalFileName the original name of the file to extract the extension
     * @return the relative path to the saved image (save this in your database)
     * @throws IOException if the file cannot be saved
     */
    public String saveImage(InputStream inputStream, String originalFileName) throws IOException {
        LocalDate today = LocalDate.now();
        Path dateDirectory = rootPath.resolve(
                today.getYear()
                        + File.separator
                        + String.format("%02d", today.getMonthValue())
                        + String.format("%02d", today.getDayOfMonth()));

        Files.createDirectories(dateDirectory);

        String extension = getFileExtension(originalFileName);
        String storedName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
        Path filePath = dateDirectory.resolve(storedName);

        try (OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW)) {
            StreamUtils.copy(inputStream, outputStream);
        }

        // Return the relative path so it can be passed exactly as-is to getImageResource later
        return rootPath.relativize(filePath).toString();
    }

    /**
     * Loads an image as a Resource, suitable for returning in an HTTP response.
     *
     * @param storedPath the relative path to the image (returned by saveImage)
     * @return a Resource representing the image file
     * @throws IOException if the file cannot be found or read
     */
    public Resource getImageResource(String storedPath) throws IOException {

        Path filePath = rootPath.resolve(storedPath).normalize();
        Path normalizedRootPath = rootPath.normalize().toAbsolutePath();

        // Critical security check: prevents directory traversal attacks
        if (!filePath.startsWith(normalizedRootPath)) {
            throw new SecurityException("Access Denied: Path is outside the storage root");
        }

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Image not found");
        }

        return new UrlResource(filePath.toUri());
    }

    /**
     * Helper method to extract the file extension.
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        return lastDot == -1 ? "" : fileName.substring(lastDot + 1);
    }
}
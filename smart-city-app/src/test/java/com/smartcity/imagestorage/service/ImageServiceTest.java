package com.smartcity.imagestorage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageServiceTest {

    @TempDir
    Path tempDir;

    private ImageService imageService;

    @BeforeEach
    void setUp() {
        imageService = new ImageService(tempDir.toString());
    }

    @Test
    void saveImage_andGetImageResource_roundTrip() throws IOException {
        byte[] content = new byte[]{1, 2, 3, 4};
        String storedPath = imageService.saveImage(new ByteArrayInputStream(content), "photo.png");

        assertThat(storedPath).isNotBlank();
        assertThat(Files.exists(tempDir.resolve(storedPath.replace('/', '\\')))).isTrue();

        Resource resource = imageService.getImageResource(storedPath);
        assertThat(resource.exists()).isTrue();
        assertThat(resource.contentLength()).isEqualTo(content.length);
    }

    @Test
    void getImageResource_rejectsPathTraversal() {
        assertThatThrownBy(() -> imageService.getImageResource("../secret.txt"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void getImageResource_throwsWhenMissing() {
        assertThatThrownBy(() -> imageService.getImageResource("missing/file.png"))
                .isInstanceOf(FileNotFoundException.class);
    }
}

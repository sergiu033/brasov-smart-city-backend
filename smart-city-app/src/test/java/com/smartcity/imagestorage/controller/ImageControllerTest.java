package com.smartcity.imagestorage.controller;

import com.smartcity.imagestorage.service.ImageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    @Test
    void downloadImage_returnsResource() throws IOException {
        Resource resource = new ByteArrayResource(new byte[]{1, 2});
        when(imageService.getImageResource("2026/0517/a.png")).thenReturn(resource);

        ResponseEntity<Resource> result = imageController.downloadImage("2026/0517/a.png");

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isEqualTo(resource);
    }

    @Test
    void downloadImage_rejectsBlankPath() {
        assertThatThrownBy(() -> imageController.downloadImage("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obligatorie");
    }
}

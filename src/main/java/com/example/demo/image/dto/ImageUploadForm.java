package com.example.demo.image.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public record ImageUploadForm(
        @NotBlank(message = "Title can not be empty")
        String title,

        String description,

        @NotNull(message = "File is required.")
        @RequestParam("multipartFile") MultipartFile multipartFile

) {
}

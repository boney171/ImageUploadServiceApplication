package com.example.demo.image.dto;

import java.util.Date;

public record ImageResponseModel(
        Long id,
        String title,
        String path,
        Date dateUploaded
) {
}

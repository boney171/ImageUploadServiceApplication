package com.example.demo.user.dto;

import com.example.demo.image.dto.ImageResponseModel;

import java.util.List;

public record UserProfileResponseModel(Long id, String username, String name, List<ImageResponseModel> images) {
}

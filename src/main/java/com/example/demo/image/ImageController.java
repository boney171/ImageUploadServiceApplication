package com.example.demo.image;

import com.example.demo.auth.security.UserPrincipal;
import com.example.demo.image.dto.ImageResponseModel;
import com.example.demo.image.dto.ImageUploadForm;
import com.example.demo.imgur.ImgurHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/images")
public class ImageController {
    @Autowired
    private ImgurHandler imgurHandler;

    @Autowired
    private ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageResponseModel uploadImage(Authentication authentication, @Valid @ModelAttribute ImageUploadForm imageForm) throws IOException, InterruptedException, ExecutionException {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        return imageService.saveImage(imageForm, userId);
    }

    @DeleteMapping("/{image_id}")
    public ResponseEntity<Map<String, String>> deleteImage(Authentication authentication, @PathVariable("image_id") Long imageId) throws IOException, ExecutionException, InterruptedException {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        return ResponseEntity.ok(imageService.deleteUserImage(userId, imageId));
    }
}

package com.example.demo.image;

import com.example.demo.image.dto.ImageResponseModel;
import com.example.demo.image.dto.ImageUploadForm;
import com.example.demo.imgur.ImgurHandler;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ImgurHandler imgurHandler;
    @Autowired
    private UserRepository userRepository;

    public ImageResponseModel saveImage(ImageUploadForm imageUploadForm, Long userId) throws IOException, InterruptedException, ExecutionException {
        Map<String, Object> response = imgurHandler.upload(imageUploadForm.multipartFile(), imageUploadForm.title(), imageUploadForm.description()).get();

        if (response == null || !response.containsKey("data")) {
            throw new RuntimeException("Imgur response body is null or missing 'data' key");
        }

        // Extract only the `data` part of the response
        Map<String, Object> data = (Map<String, Object>) response.get("data");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist"));

        // Create and save Image entity
        Image newImage = new Image(
                null,
                imageUploadForm.title(),
                (String) data.get("id"),
                (String) data.get("deletehash"),
                user,
                (String) data.get("link"),
                new Date()
        );

        imageRepository.save(newImage);
        return newImage.toResponseModel();
    }

    public Map<String,String> deleteUserImage(Long userId, Long imageId) throws IOException, ExecutionException, InterruptedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist"));

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image doesn't exist"));

        Map<String, Object> response = imgurHandler.deleteImageFromImgur( image.getDeleteHash()).get();

        if(!response.get("status").equals(200)){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete image from imgur");
        }

        imageRepository.deleteById(imageId);

        Map<String, String> httpResponse = new HashMap<>();
        //Make a response model for this
        httpResponse.put("status", String.valueOf(HttpStatus.OK.value()));
        httpResponse.put("message", String.valueOf("Image deleted successfully"));
        return httpResponse;
    }
}
//Use springboot to generate API doc
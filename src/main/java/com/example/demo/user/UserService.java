package com.example.demo.user;

import com.example.demo.image.Image;
import com.example.demo.image.ImageRepository;
import com.example.demo.image.dto.ImageResponseModel;
import com.example.demo.user.dto.UserProfileResponseModel;
import com.example.demo.user.dto.UserRegistrationRequestModel;
import com.example.demo.user.dto.UserResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, ImageRepository imageRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseModel createUser(UserRegistrationRequestModel userRegistrationModel) {
        Optional<User> existingUser = userRepository.findByUsername(userRegistrationModel.username());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username " + userRegistrationModel.username() + " already exists");
        }

        String hashedPassword = passwordEncoder.encode(userRegistrationModel.password());
        User user = userRegistrationModel.toUserEntity();
        user.setPassword(hashedPassword);

        userRepository.save(user);

        logger.info("User created: {}", userRegistrationModel.username());
        return user.toUserResponseModel();
    }

    public UserProfileResponseModel getUserProfile(Long userId) {
        User user = userRepository.findById(userId).get();

        List<ImageResponseModel> userImageResponseModels = imageRepository.findAllByUserId(userId)
                .stream()
                .map(Image::toResponseModel)
                .toList();

        return new UserProfileResponseModel(user.getId(), user.getUsername(), user.getName(), userImageResponseModels);
    }

}

package com.example.demo.user;

import com.example.demo.image.Image;
import com.example.demo.image.ImageRepository;
import com.example.demo.image.dto.ImageResponseModel;
import com.example.demo.user.dto.UserProfileResponseModel;
import com.example.demo.user.dto.UserRegistrationRequestModel;
import com.example.demo.user.dto.UserResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationRequestModel userRegistrationModel;
    private User user;

    @BeforeEach
    void setUp() {
        userRegistrationModel = new UserRegistrationRequestModel("testuser", "password123", "Test User");
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedPassword");
        user.setName("Test User");
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseModel response = userService.createUser(userRegistrationModel);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.username());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_UsernameAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.createUser(userRegistrationModel));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Username testuser already exists", exception.getReason());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserProfile_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Date uploadDate = new Date();
        Image image = new Image();
        image.setId(null);
        image.setTitle("Test Image");
        image.setPath("/images/test.jpg");
        image.setDate(uploadDate);
        image.setUser(user);
        List<Image> images = List.of(image);
        when(imageRepository.findAllByUserId(userId)).thenReturn(images);

        // Act
        UserProfileResponseModel response = userService.getUserProfile(userId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals("testuser", response.username());
        assertEquals("Test User", response.name());
        assertEquals(1, response.images().size());

        ImageResponseModel imageResponse = response.images().get(0);
        assertNull(imageResponse.id());
        assertEquals("Test Image", imageResponse.title());
        assertEquals("/images/test.jpg", imageResponse.path());
        assertEquals(uploadDate, imageResponse.dateUploaded());

        verify(userRepository, times(1)).findById(userId);
        verify(imageRepository, times(1)).findAllByUserId(userId);
    }


    @Test
    void getUserProfile_NoImages_ReturnsEmptyList() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(imageRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        UserProfileResponseModel response = userService.getUserProfile(userId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals("testuser", response.username());
        assertEquals("Test User", response.name());
        assertTrue(response.images().isEmpty());
        verify(userRepository, times(1)).findById(userId);
        verify(imageRepository, times(1)).findAllByUserId(userId);
    }
}
package com.example.demo.image;

import com.example.demo.image.dto.ImageResponseModel;
import com.example.demo.image.dto.ImageUploadForm;
import com.example.demo.imgur.ImgurHandler;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImageServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ImageServiceTest.class);

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImgurHandler imgurHandler;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ImageService imageService;

    private ImageUploadForm imageUploadForm;
    private User user;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        multipartFile = mock(MultipartFile.class);
        imageUploadForm = new ImageUploadForm( "Test Image", "Description", multipartFile);
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        logger.info("Test setup completed: multipartFile and user initialized.");
    }

    @Test
    void saveImage_Success() throws IOException, InterruptedException, ExecutionException {
        // Arrange
        Long userId = 1L;
        Map<String, Object> imgurData = new HashMap<>();
        imgurData.put("id", "imgurId123");
        imgurData.put("deletehash", "deleteHash123");
        imgurData.put("link", "http://imgur.com/test.jpg");

        Map<String, Object> imgurResponse = new HashMap<>();
        imgurResponse.put("data", imgurData);
        imgurResponse.put("success", true);
        imgurResponse.put("status", 200);

        when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3}); // Moved here
        when(imgurHandler.upload(multipartFile, "Test Image", "Description"))
                .thenReturn(CompletableFuture.completedFuture(imgurResponse));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Image savedImage = new Image(1L, "Test Image", "imgurId123", "deleteHash123", user, "http://imgur.com/test.jpg", new Date());
        when(imageRepository.save(any(Image.class))).thenReturn(savedImage);

        logger.info("Starting saveImage_Success test for userId: {}", userId);

        // Act
        ImageResponseModel response = imageService.saveImage(imageUploadForm, userId);

        // Assert
        logger.info("Response received: {}", response);
        assertNotNull(response);
        assertEquals("Test Image", response.title());
        assertEquals("http://imgur.com/test.jpg", response.path());
        verify(imgurHandler, times(1)).upload(multipartFile, "Test Image", "Description");
        verify(userRepository, times(1)).findById(userId);
        verify(imageRepository, times(1)).save(any(Image.class));
        logger.info("saveImage_Success test completed successfully.");
    }

    @Test
    void saveImage_UserNotFound_ThrowsException() throws IOException, InterruptedException, ExecutionException {
        // Arrange
        Long userId = 1L;
        Map<String, Object> imgurData = new HashMap<>();
        imgurData.put("id", "imgurId123");
        imgurData.put("deletehash", "deleteHash123");
        imgurData.put("link", "http://imgur.com/test.jpg");

        Map<String, Object> imgurResponse = new HashMap<>();
        imgurResponse.put("data", imgurData);
        imgurResponse.put("success", true);
        imgurResponse.put("status", 200);

        when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3}); // Needed for ImgurHandler
        when(imgurHandler.upload(multipartFile, "Test Image", "Description"))
                .thenReturn(CompletableFuture.completedFuture(imgurResponse));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        logger.info("Starting saveImage_UserNotFound_ThrowsException test for userId: {}", userId);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> imageService.saveImage(imageUploadForm, userId));
        logger.info("Exception thrown with status: {}, reason: {}", exception.getStatusCode(), exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User doesn't exist", exception.getReason());
        verify(imgurHandler, times(1)).upload(multipartFile, "Test Image", "Description");
        verify(userRepository, times(1)).findById(userId);
        verify(imageRepository, never()).save(any(Image.class));
        logger.info("saveImage_UserNotFound_ThrowsException test completed.");
    }

    @Test
    void saveImage_ImgurResponseNull_ThrowsException() throws IOException, InterruptedException, ExecutionException {
        // Arrange
        Long userId = 1L;
        when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3}); // Moved here
        when(imgurHandler.upload(multipartFile, "Test Image", "Description"))
                .thenReturn(CompletableFuture.completedFuture(null));

        logger.info("Starting saveImage_ImgurResponseNull_ThrowsException test for userId: {}", userId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> imageService.saveImage(imageUploadForm, userId));
        logger.info("Exception thrown with message: {}", exception.getMessage());
        assertEquals("Imgur response body is null or missing 'data' key", exception.getMessage());
        verify(imgurHandler, times(1)).upload(multipartFile, "Test Image", "Description");
        verify(userRepository, never()).findById(anyLong());
        verify(imageRepository, never()).save(any(Image.class));
        logger.info("saveImage_ImgurResponseNull_ThrowsException test completed.");
    }

    // Other tests (deleteUserImage_*) remain unchanged as they weren't reported as failing
}
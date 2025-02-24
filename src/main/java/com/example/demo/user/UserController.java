package com.example.demo.user;

import com.example.demo.auth.security.UserPrincipal;
import com.example.demo.user.dto.UserProfileResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user-profile")
    public ResponseEntity<UserProfileResponseModel> getProfile(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();

        logger.info("Fetching profile for user ID: {}", userId);
        UserProfileResponseModel userProfile = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }
}
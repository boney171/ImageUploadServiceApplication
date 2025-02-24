package com.example.demo.auth.security;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtTokenProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JWTUtil jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Get Authorization header
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Long userId = jwtTokenProvider.getUserIdFromToken(token);

            if (userId != null) {
                Optional<User> user = userRepository.findById(userId);

                if (user.isPresent()) {
                    UserPrincipal userPrincipal = new UserPrincipal(userId, user.get().getUsername());

                    var auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, null);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}

package com.replan;
import com.replan.config.JwtAuthenticationFilter;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.UserEntity;
import com.replan.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize Spring's mock classes for HTTP
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();

        // Create the filter with our mocked dependencies
        filter = new JwtAuthenticationFilter(jwtUtil, userRepository);

        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_withValidToken_shouldSetAuthentication() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        String email = "user@example.com";

        // Set the Authorization header
        request.addHeader("Authorization", "Bearer " + validToken);

        // Mock JWT validation and extraction
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(validToken)).thenReturn(email);

        // Mock user retrieval
        UserEntity user = new UserEntity();
        UUID uuid = UUID.randomUUID();
        user.setId(uuid);
        user.setEmail(email);
        user.setUsername("testUser");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);
    }

    @Test
    void doFilter_withInvalidToken_shouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Set the Authorization header
        request.addHeader("Authorization", "Bearer " + invalidToken);

        // Mock JWT validation failure
        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtil, never()).getEmailFromToken(anyString());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void doFilter_withNoToken_shouldNotSetAuthentication() throws ServletException, IOException {
        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtil, never()).validateToken(anyString());
        verify(jwtUtil, never()).getEmailFromToken(anyString());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void doFilter_withNonBearerToken_shouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==");

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtUtil, never()).validateToken(anyString());
        verify(jwtUtil, never()).getEmailFromToken(anyString());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void doFilter_withTokenButUserNotFound_shouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        String email = "nonexistent@example.com";

        // Set the Authorization header
        request.addHeader("Authorization", "Bearer " + validToken);

        // Mock JWT validation and extraction
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(validToken)).thenReturn(email);

        // Mock user not found
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_withExceptionDuringProcess_shouldContinueFilterChain() throws ServletException, IOException {
        // Arrange
        String token = "exception.token";

        // Set the Authorization header
        request.addHeader("Authorization", "Bearer " + token);

        // Mock JWT validation to throw exception
        when(jwtUtil.validateToken(token)).thenThrow(new RuntimeException("Test exception"));

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
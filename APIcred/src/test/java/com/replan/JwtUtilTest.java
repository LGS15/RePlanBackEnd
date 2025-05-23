package com.replan;

import com.replan.security.JwtUtil;
import io.jsonwebtoken.Claims;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String SECRET = "testSecretKeyWithAtLeast32CharactersForAlgorithmRequirements";
    private static final long EXPIRATION = 3600000; // 1 hour in milliseconds

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Because they're private this gets done
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", EXPIRATION);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        // Arrange
        String email = "test@example.com";

        // Act
        String token = jwtUtil.generateToken(email);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(email, jwtUtil.getEmailFromToken(token));
    }

    @Test
    void validateToken_withValidToken_shouldReturnTrue() {
        // Arrange
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_withExpiredToken_shouldReturnFalse() {
        // Arrange
        String email = "test@example.com";

        JwtUtil jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        // set the expiration window to -1 second so the token is already expired
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", -1000L);

        // Act
        String token = jwtUtil.generateToken(email);
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertFalse(isValid);
    }
    @Test
    void validateToken_withInvalidSignature_shouldReturnFalse() {
        // Arrange
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        // "Corrupt" the token by changing the last character
        String invalidToken = token.substring(0, token.length() - 1) + (token.charAt(token.length() - 1) == 'a' ? 'b' : 'a');

        // Act
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_withMalformedToken_shouldReturnFalse() {
        // Arrange
        String malformedToken = "this.is.not.a.valid.jwt.token";

        // Act
        boolean isValid = jwtUtil.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void getEmailFromToken_withValidToken_shouldReturnEmail() {
        // Arrange
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        // Act
        String extractedEmail = jwtUtil.getEmailFromToken(token);

        // Assert
        assertEquals(email, extractedEmail);
    }

    @Test
    void getExpirationDateFromToken_withValidToken_shouldReturnExpirationDate() {
        // Arrange
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        // Act
        Date expirationDate = jwtUtil.getExpirationDateFromToken(token);

        // Assert
        assertNotNull(expirationDate);

        long expectedExpiration = System.currentTimeMillis() + EXPIRATION;
        assertTrue(Math.abs(expirationDate.getTime() - expectedExpiration) < 5000); // Allow 5 seconds tolerance
    }

    @Test
    void refreshToken_withValidNonExpiryToken_shouldReturnSameToken() {
        // Arrange
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        // Act
        String refreshedToken = jwtUtil.refreshToken(token);

        // Assert
        assertEquals(token, refreshedToken);
    }

    @Test
    void refreshToken_withInvalidToken_shouldReturnNull() {
        // Arrange
        String invalidToken = "invalid.token.format";

        // Act
        String refreshedToken = jwtUtil.refreshToken(invalidToken);

        // Assert
        assertNull(refreshedToken);
    }

    @Test
    void isTokenCloseToExpiry_withFreshToken_shouldReturnFalse() {
        // Arrange
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        // Act
        boolean isCloseToExpiry = jwtUtil.isTokenCloseToExpiry(token);

        // Assert
        assertFalse(isCloseToExpiry);
    }

    @Test
    void isTokenCloseToExpiry_withInvalidToken_shouldReturnTrue() {
        // Arrange
        String invalidToken = "invalid.token.format";

        // Act
        boolean isCloseToExpiry = jwtUtil.isTokenCloseToExpiry(invalidToken);

        // Assert
        assertTrue(isCloseToExpiry);
    }

    @Test
    void getClaimFromToken_shouldExtractClaim() {
        // Arrange
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);

        // Act - Here we're testing the function that extracts specific claims
        String subject = jwtUtil.getClaimFromToken(token, Claims::getSubject);

        // Assert
        assertEquals(email, subject);
    }

    @Test
    void refreshToken_withExpiringSoonToken_shouldReturnValidToken() {
        String email = "test@example.com";

        // Create a new instance of JwtUtil with a test method to simulate near-expiry token
        JwtUtil testJwtUtil = new JwtUtil() {
            @Override
            public boolean isTokenCloseToExpiry(String token) {
                return true; // Simulate token being close to expiry
            }
        };

        // Set the necessary fields
        ReflectionTestUtils.setField(testJwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(testJwtUtil, "jwtExpiration", EXPIRATION);

        // Generate a token
        String token = testJwtUtil.generateToken(email);

        // Test the refresh token functionality
        String refreshedToken = testJwtUtil.refreshToken(token);

        // Verify that refreshToken returns a valid token
        assertNotNull(refreshedToken);
        assertTrue(refreshedToken.contains("."));

        // Verify the refreshed token can be validated
        assertTrue(testJwtUtil.validateToken(refreshedToken));

        // The token should contain the same email
        assertEquals(email, testJwtUtil.getEmailFromToken(refreshedToken));
    }


    @Test
    void refreshToken_shouldRefreshTokenBasedOnExpirationTime() {
        // Test with a valid token
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);
        String refreshedToken = jwtUtil.refreshToken(token);

        // Even if the same token is returned, it should be valid
        assertNotNull(refreshedToken);
        assertTrue(jwtUtil.validateToken(refreshedToken));

        // The token should contain the expected email
        assertEquals(email, jwtUtil.getEmailFromToken(refreshedToken));
    }

    @Test
    void getEmailFromToken_withInvalidToken_shouldHandleException() {
        // Arrange
        String invalidToken = "invalid.token";

        // Act & Assert
        try {
            jwtUtil.getEmailFromToken(invalidToken);
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            assertNotNull(e); //validate the exception
        }
    }
}
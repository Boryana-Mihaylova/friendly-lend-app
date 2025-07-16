package app.security;

import app.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthorizationServiceTest {

    private final AuthorizationService authService = new AuthorizationService();

    @Test
    void testCanAccessUser_whenUserIsSame_shouldReturnTrue() {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata metadata = new AuthenticationMetadata(userId, "user", "pass", UserRole.USER, true);
        Authentication auth = new TestingAuthenticationToken(metadata, null);

        boolean result = authService.canAccessUser(userId, auth);

        assertTrue(result);
    }

    @Test
    void testCanAccessUser_whenAdmin_shouldReturnTrue() {
        UUID someOtherUserId = UUID.randomUUID();
        AuthenticationMetadata metadata = new AuthenticationMetadata(UUID.randomUUID(), "admin", "pass", UserRole.ADMIN, true);
        Authentication auth = new TestingAuthenticationToken(metadata, null);

        boolean result = authService.canAccessUser(someOtherUserId, auth);

        assertTrue(result);
    }

    @Test
    void testCanAccessUser_whenDifferentUserAndNotAdmin_shouldReturnFalse() {
        UUID someOtherUserId = UUID.randomUUID();
        AuthenticationMetadata metadata = new AuthenticationMetadata(UUID.randomUUID(), "user", "pass", UserRole.USER, true);
        Authentication auth = new TestingAuthenticationToken(metadata, null);

        boolean result = authService.canAccessUser(someOtherUserId, auth);

        assertFalse(result);
    }

    @Test
    void testCanAccessUser_whenNoAuthentication_shouldReturnFalse() {
        boolean result = authService.canAccessUser(UUID.randomUUID(), null);
        assertFalse(result);
    }
}

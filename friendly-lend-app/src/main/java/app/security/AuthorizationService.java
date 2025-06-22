package app.security;

import app.user.model.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("authz")
public class AuthorizationService {
    public boolean canAccessUser(UUID id, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticationMetadata authMeta)) {
            return false;
        }

        return authMeta.getUserId().equals(id) || authMeta.getRole().equals(UserRole.ADMIN);
    }
}

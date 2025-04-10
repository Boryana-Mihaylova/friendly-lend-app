package app.web;


import app.user.model.User;
import app.user.model.UserRole;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class TestBuilder {

    public static User aRandomUser() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("User")
                .password("123123")
                .role(UserRole.USER)
                .email("bob@bob.com")
                .isActive(true)
                .build();

        return user;
    }
}

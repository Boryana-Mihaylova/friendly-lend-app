package app.user;


import app.exception.DomainException;
import app.notification.repository.NotificationRepository;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationRepository notificationRepository;


    @InjectMocks
    private UserService userService;


    @Test
    void givenMissingUserFromDatabase_whenLoadUserByUsername_thenExceptionIsThrown() {


        String username = "sia";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());


        assertThrows(DomainException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    void givenExistingUsersInDatabase_whenGetAllUsers_thenReturnThemAll() {

        List<User> userList = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(userList);

        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
    }


    @Test
    void givenNewUser_whenRegister_thenUserIsSavedSuccessfully() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("newUser")
                .email("newuser@example.com")
                .password("password123")
                .build();

        when(userRepository.findByUsernameOrEmail("newUser", "newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        userService.register(registerRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenExistingUsernameOrEmail_whenRegister_thenRuntimeExceptionIsThrown() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("sia")
                .password("asdfgh")
                .email("sia@sia.bg")
                .build();

        when(userRepository.findByUsernameOrEmail("sia", "sia@sia.bg"))
                .thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> userService.register(registerRequest));
    }

    @Test
    void givenUserExists_whenEditUserDetails_thenUserDetailsAreUpdated() {

        UUID userId = UUID.randomUUID();
        UserEditRequest userEditRequest = UserEditRequest.builder()
                .firstName("sia")
                .lastName("white")
                .email("sia@sia.bg")
                .build();

        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.editUserDetails(userId, userEditRequest);


        assertEquals("sia", user.getFirstName());
        assertEquals("white", user.getLastName());
        assertEquals("sia@sia.bg", user.getEmail());

    }

}

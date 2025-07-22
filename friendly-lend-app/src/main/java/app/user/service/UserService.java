package app.user.service;


import app.exception.DomainException;
import app.exception.UsernameAlreadyExistException;
import app.notification.model.Notification;
import app.notification.repository.NotificationRepository;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Slf4j
@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationRepository notificationRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationRepository = notificationRepository;
    }


    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void register(RegisterRequest registerRequest){

        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail());

        if (optionalUser.isPresent()){
            throw new UsernameAlreadyExistException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .isActive(true)
                .build();

        userRepository.save(user);

        Notification notification = Notification.builder()
                .user(user)
                .message("Welcome to FriendlyLend! Items inactive for over 60 days may be archived to improve visibility on the platform. You’ll be notified in your profile if this applies.")
                .createdAt(new Date())
                .read(false)
                .build();

        notificationRepository.save(notification);

    }


    @CacheEvict(value = "users", allEntries = true)
    public void editUserDetails(UUID userId, UserEditRequest userEditRequest) {

        User user = getById(userId);

        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setEmail(userEditRequest.getEmail());


        userRepository.save(user);
    }



    @Cacheable("users")
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public User getById(UUID id) {

        Optional<User> user = userRepository.findById(id);

        user.orElseThrow(() -> new DomainException("User with id [%s] does not exist.".formatted(id)));

        return user.get();
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchStatus(UUID userId) {

        User user = getById(userId);


        user.setActive(!user.isActive());
        userRepository.save(user);
    }


    @CacheEvict(value = "users", allEntries = true)
    public void switchRole(UUID userId) {

        User user = getById(userId);

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DomainException("User with this username does not exist."));

        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }

    public int getUnreadNotificationCount(UUID userId) {
        User user = getById(userId);
        return notificationRepository.findAllByUserAndReadFalse(user).size();
    }

    public List<Notification> getUnreadNotifications(UUID userId) {
        User user = getById(userId);
        return notificationRepository.findAllByUserAndReadFalse(user);
    }

    public void markAllNotificationsAsRead(UUID userId) {
        User user = getById(userId);
        var unread = notificationRepository.findAllByUserAndReadFalse(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}

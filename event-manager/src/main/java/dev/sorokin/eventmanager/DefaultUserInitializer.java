package dev.sorokin.eventmanager;

import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initUsers() {
        String DEFAULT_USER_LOGIN = "user";
        String DEFAULT_USER_PASS = "user";

        if (!userRepository.existsByLogin(DEFAULT_USER_LOGIN)) {
            UserEntity user = new UserEntity(
                    null,
                    DEFAULT_USER_LOGIN,
                    passwordEncoder.encode(DEFAULT_USER_PASS),
                    null,
                    UserRole.USER.name()
            );

            userRepository.save(user);
        }

        String DEFAULT_ADMIN_LOGIN = "admin";
        String DEFAULT_ADMIN_PASS = "admin";

        if (!userRepository.existsByLogin(DEFAULT_ADMIN_LOGIN)) {
            UserEntity admin = new UserEntity(
                    null,
                    DEFAULT_ADMIN_LOGIN,
                    passwordEncoder.encode(DEFAULT_ADMIN_PASS),
                    null,
                    UserRole.ADMIN.name()
            );

            userRepository.save(admin);
        }
    }
}

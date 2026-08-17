package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.UserRepository;
import dev.sorokin.eventmanager.security.jwt.JwtTokenManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManager authenticationManager;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtTokenManager jwtTokenManager,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenManager = jwtTokenManager;
        this.authenticationManager = authenticationManager;
    }

    public User createUser(User userToCreate) {

        if (userRepository.existsByLogin(userToCreate.login())) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        UserEntity userEntity = userMapper.mapDomainToEntity(userToCreate);

        String encodedPassword = passwordEncoder.encode(userToCreate.password());
        userEntity.setPassword(encodedPassword);

        UserEntity createdUser = userRepository.save(userEntity);

        return userMapper.mapEntityToDomain(createdUser);
    }

    public String authenticateUser(String login, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password)
        );

        return jwtTokenManager.generateJwtToken(login);
    }

    public User findUserById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow();

        return userMapper.mapEntityToDomain(userEntity);
    }

    public User getCurrentUser() {
        UserEntity currentUser;
        try {
            currentUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return userMapper.mapEntityToDomain(currentUser);
    }
}

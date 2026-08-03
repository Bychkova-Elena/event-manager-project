package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public User createUser(User userToCreate) {

        if (userRepository.existsByLogin(userToCreate.login())) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        //TODO: закешировать пароль
        UserEntity userEntity = userMapper.mapDomainToEntity(userToCreate);

        UserEntity createdUser = userRepository.save(userEntity);

        return userMapper.mapEntityToDomain(createdUser);
    }
}

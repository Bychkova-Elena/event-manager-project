package dev.sorokin.eventmanager.mapper;

import dev.sorokin.eventmanager.dto.RegisterResponseDto;
import dev.sorokin.eventmanager.dto.RegisterUserRequestDto;
import dev.sorokin.eventmanager.dto.UserByIdResponseDto;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User mapFromDtoToUser(RegisterUserRequestDto dto) {
        return new User(
                null,
                dto.getLogin(),
                dto.getPassword(),
                dto.getAge(),
                UserRole.USER.name()
        );
    }

    public RegisterResponseDto mapFromUserToDto(User user) {
        return new RegisterResponseDto(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }

    public UserEntity mapDomainToEntity(User user) {
        return new UserEntity(
                user.id(),
                user.login(),
                user.password(),
                user.age(),
                user.role()
        );
    }

    public User mapEntityToDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getLogin(),
                entity.getPassword(),
                entity.getAge(),
                entity.getRole()
        );
    }

    public UserByIdResponseDto mapDomainToUserByIdResponseDto(User user) {
        return new UserByIdResponseDto(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }
}

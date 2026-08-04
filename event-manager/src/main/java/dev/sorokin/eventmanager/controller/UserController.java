package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.dto.AuthRequestDto;
import dev.sorokin.eventmanager.dto.AuthResponseDto;
import dev.sorokin.eventmanager.dto.RegisterResponseDto;
import dev.sorokin.eventmanager.dto.RegisterUserRequestDto;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<RegisterResponseDto> registerUser(@Valid @RequestBody RegisterUserRequestDto dto) {

        User userToCreate = userMapper.mapFromDtoToUser(dto);
        User user = userService.createUser(userToCreate);
        RegisterResponseDto response = userMapper.mapFromUserToDto(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponseDto> auth(@Valid @RequestBody AuthRequestDto dto) {
        String token = userService.authenticateUser(dto.getLogin(), dto.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponseDto(token));

    }
}

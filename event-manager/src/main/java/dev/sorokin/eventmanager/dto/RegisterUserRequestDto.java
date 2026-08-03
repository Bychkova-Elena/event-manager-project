package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterUserRequestDto {

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    @NotNull
    @Min(value = 18)
    private int age;

    public RegisterUserRequestDto(String login, String password, int age) {
        this.login = login;
        this.password = password;
        this.age = age;
    }

    public @NotBlank String getLogin() {
        return login;
    }

    public void setLogin(@NotBlank String login) {
        this.login = login;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }

    @NotNull
    @Min(value = 18)
    public int getAge() {
        return age;
    }

    public void setAge(@NotNull @Min(value = 18) int age) {
        this.age = age;
    }
}

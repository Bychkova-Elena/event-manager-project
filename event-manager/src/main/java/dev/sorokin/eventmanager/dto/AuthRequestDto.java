package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequestDto {

    @NotBlank
    private String login;
    @NotBlank
    private String password;

    public AuthRequestDto(String login, String password) {
        this.login = login;
        this.password = password;
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
}

package dev.sorokin.eventmanager.model;

public record User(
        Long id,
        String login,
        String password,
        int age,
        String role
) {
}

package dev.sorokin.eventnotificator.security.jwt;

public record UserPrincipal(Long userId, String login, String role) {}


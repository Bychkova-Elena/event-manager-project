package dev.sorokin.eventmanager.dto;

public class UserByIdResponseDto {
    private Long id;
    private String login;
    private int age;
    private String role;

    public UserByIdResponseDto(Long id, String login, int age, String role) {
        this.id = id;
        this.login = login;
        this.age = age;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

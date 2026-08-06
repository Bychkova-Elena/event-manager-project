package dev.sorokin.eventmanager.entity;

import dev.sorokin.eventmanager.enums.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "\"user\"")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "age")
    private Integer age;

    @Column(name = "role")
    private String role;

    private static final Integer DEFAULT_AGE = 18;

    public UserEntity(String login, String password) {
        this.login = login;
        this.password = password;
        this.age = DEFAULT_AGE;
        this.role = UserRole.USER.name();
    }

    public UserEntity(String login, String password, Integer age) {
        this.login = login;
        this.password = password;
        this.age = age != null ? age : DEFAULT_AGE;
        this.role = UserRole.USER.name();
    }

    public UserEntity(Long id, String login, String password, Integer age, String role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.age = age != null ? age : DEFAULT_AGE;
        this.role = role != null ? role : UserRole.USER.name();
    }

    public UserEntity() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

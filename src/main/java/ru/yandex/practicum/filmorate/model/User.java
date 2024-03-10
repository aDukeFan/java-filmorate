package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;


@ToString
@Getter
@Setter
public class User {
    private int id;
    private String name;
    private String email;
    private String login;
    private LocalDate birthday;

    public User(String login, String email, LocalDate birthday) {
        this.name = login;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

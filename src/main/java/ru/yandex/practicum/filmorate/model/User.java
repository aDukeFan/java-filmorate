package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@ToString
@Getter
@Setter
@Accessors(chain = true)
public class User {
    private int id;
    private String name;
    @NotNull
    @Email
    private String email;
    @NotEmpty
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9]*$")
    private String login;
    @Past
    private LocalDate birthday;
    private Set<Integer> friendsId = new HashSet<>();

    public void addFriend(int id) {
        friendsId.add(id);
    }

    public void removeFriend(int id) {
        friendsId.remove(id);
    }
}

package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@ToString
@Getter
@Setter
@Accessors(chain = true)
public class User {
    private Integer id;
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
    private Set<Integer> friends = new LinkedHashSet<>();
}

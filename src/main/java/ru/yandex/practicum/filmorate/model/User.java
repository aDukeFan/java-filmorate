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

    public static Builder builder() {
        return new User().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Builder id(Integer id) {
            User.this.id = id;
            return this;
        }

        public Builder email(String email) {
            User.this.email = email;
            return this;
        }

        public Builder login(String login) {
            User.this.login = login;
            return this;
        }

        public Builder name(String name) {
            User.this.name = name;
            return this;
        }

        public Builder birthday(LocalDate birthday) {
            User.this.birthday = birthday;
            return this;
        }

        public Builder friends(Set<Integer> friends) {
            User.this.friends = friends;
            return this;
        }

        public User build() {
            return User.this;
        }
    }
}

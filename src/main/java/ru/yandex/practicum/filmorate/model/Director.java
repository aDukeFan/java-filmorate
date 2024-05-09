package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
public class Director {
    private int id;
    @NotNull
    @NotBlank
    @NotEmpty
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Director)) return false;
        Director director = (Director) o;
        return id == director.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static Director.Builder builder() {
        return new Director().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Director.Builder id(int id) {
            Director.this.id = id;
            return this;
        }

        public Director.Builder name(String name) {
            Director.this.name = name;
            return this;
        }

        public Director build() {
            return Director.this;
        }
    }
}

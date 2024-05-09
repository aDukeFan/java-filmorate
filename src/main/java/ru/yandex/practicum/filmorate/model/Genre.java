package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
public class Genre {
    private int id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;
        Genre genre = (Genre) o;
        return id == genre.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static Genre.Builder builder() {
        return new Genre().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Genre.Builder id(int id) {
            Genre.this.id = id;
            return this;
        }

        public Genre.Builder name(String name) {
            Genre.this.name = name;
            return this;
        }

        public Genre build() {
            return Genre.this;
        }
    }
}

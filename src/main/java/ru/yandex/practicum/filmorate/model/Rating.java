package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Rating {

    int id;
    String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating)) return false;
        Rating rating = (Rating) o;
        return id == rating.id && Objects.equals(name, rating.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public static Rating.Builder builder() {
        return new Rating().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Rating.Builder id(Integer id) {
            Rating.this.id = id;
            return this;
        }

        public Rating.Builder name(String name) {
            Rating.this.name = name;
            return this;
        }

        public Rating build() {
            return Rating.this;
        }
    }
}

package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
public class Rating {

    private int id;
    private String name;

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
}

package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.Validator.ReleaseDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Integer id;
    @NotEmpty(message = "Name must be set")
    @NotNull(message = "Must have a name")
    String name;
    @ReleaseDate(message = "The date of release must be after 28 December 1895")
    LocalDate releaseDate;
    @Size(max = 200, message = "Description is too long (max 200 chars).")
    String description;
    @Positive(message = "Duration must be positive.")
    int duration;
    int rate;
    Rating mpa;
    Set<Director> directors = new HashSet<>();
    Set<Genre> genres = new LinkedHashSet<>();
    Set<Integer> likes = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.yandex.practicum.filmorate.util.ReleaseDate;

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
public class Film {
    private Integer id;
    @NotEmpty(message = "Name must be set")
    @NotNull(message = "Must have a name")
    private String name;
    @ReleaseDate(message = "The date of release must be after 28 December 1895")
    private LocalDate releaseDate;
    @Size(max = 200, message = "Description is too long (max 200 chars).")
    private String description;
    @Positive(message = "Duration must be positive.")
    private int duration;
    private int rate;
    private Rating mpa;
    private Set<Director> directors = new HashSet<>();
    private Set<Genre> genres = new LinkedHashSet<>();
    private Set<Integer> likes = new LinkedHashSet<>();

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

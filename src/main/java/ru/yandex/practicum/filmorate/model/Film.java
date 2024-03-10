package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.util.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Film.
 */
@Getter
@Setter
@ToString
public class Film {
    private int id;
    @NotEmpty(message = "Name must be set")
    @NotNull(message = "Must have a name")
    private String name;
    @Size(max = 200, message = "Description is too long (max 200 chars).")
    private String description;
    @Past
    @ReleaseDate(message = "The date of release must be after 28 December 1985 and before now")
    private LocalDate releaseDate;
    @Positive(message = "Duration must be positive.")
    private long duration;

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Film)) return false;
        Film film = (Film) o;
        return id == film.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

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
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Film {
    private int id;
    @NotEmpty(message = "Name must be set")
    @NotNull(message = "Must have a name")
    private String name;
    @Size(max = 200, message = "Description is too long (max 200 chars).")
    private String description;
    @ReleaseDate(message = "The date of release must be after 28 December 1985 and before now")
    private LocalDate releaseDate;
    @Positive(message = "Duration must be positive.")
    private int duration;
    private Set<Integer> likes = new HashSet<>();

    public void addLike(int id) {
        likes.add(id);
    }

    public void removeLike(int id) {
        likes.remove(id);
    }
}

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


    public static Film.Builder builder() {
        return new Film().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Film.Builder id(Integer id) {
            Film.this.id = id;
            return this;
        }

        public Film.Builder description(String description) {
            Film.this.description = description;
            return this;
        }

        public Film.Builder releaseDate(LocalDate releaseDate) {
            Film.this.releaseDate = releaseDate;
            return this;
        }

        public Film.Builder name(String name) {
            Film.this.name = name;
            return this;
        }

        public Film.Builder duration(Integer duration) {
            Film.this.duration = duration;
            return this;
        }

        public Film.Builder directors(Set<Director> directors) {
            Film.this.directors = directors;
            return this;
        }

        public Film.Builder likes(Set<Integer> likes) {
            Film.this.likes = likes;
            return this;
        }

        public Film.Builder genres(Set<Genre> genres) {
            Film.this.genres = genres;
            return this;
        }

        public Builder mpa(Rating mpa) {
            Film.this.mpa = mpa;
            return this;
        }

        public Film build() {
            return Film.this;
        }
    }
}

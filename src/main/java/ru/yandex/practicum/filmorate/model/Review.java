package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Review {
    Integer reviewId;
    @NotNull
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Integer userId;
    @NotNull
    Integer filmId;
    int useful = 0;

}

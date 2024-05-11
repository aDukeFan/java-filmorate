package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class Review {
    private Integer reviewId;
    @NotNull
    private String content;
    @NotNull(message = "isPositive shouldn't be null")
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private int useful = 0;

}

package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class Review {
    Integer reviewId;
    String content;
    @NotNull(message = "isPositive shouldn't be null")
    Boolean isPositive;
    @Positive(message = "userId must be positive")
    Integer userId;
    @Positive(message = "filmId must be positive")
    Integer filmId;
    Integer useful;

}

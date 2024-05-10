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
    private Integer reviewId;
    private String content;
    @NotNull(message = "isPositive shouldn't be null")
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private int useful = 0;

}

package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingService {

    Rating create(Rating rating);

    Rating update(Rating rating);

    Rating getById(int id);

    List<Rating> getAll();

    void remove(int id);
}

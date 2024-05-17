package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film create(Film film);

    Film update(Film film);

    Film getById(int id);

    List<Film> findAll();

    Film addLike(int filmId, int userId);

    Film removeLike(int filmId, int userId);

    List<Film> getTopPopularFilms(int count, int genreId, int year);

    List<Film> getFilmsByDirector(int id, String typeOfSort);

    List<Film> getFilmsByDirectorOrTitleByQuery(String query, String param);

    void delFilmById(int filmId);

    List<Film> getCommonFilms(int userId, int friendId);
}

package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {

    private FilmRepository filmRepository;

    @Override
    public Film create(Film film) {
        return filmRepository.create(film);
    }

    @Override
    public Film update(Film film) {
        return filmRepository.update(film);
    }

    @Override
    public Film getById(int id) {
        return filmRepository.getById(id);
    }

    @Override
    public List<Film> findAll() {
        log.info("Films are showed");
        return filmRepository.findAll();
    }

    @Override
    public Film addLike(int filmId, int userId) {
        return filmRepository.addLike(filmId, userId);
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        return filmRepository.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getTopPopularFilms(int count) {
        return filmRepository.getTopPopularFilms(count);
    }

    @Override
    public List<Film> getFilmsByDirector(int id, String typeOfSort) {
        return filmRepository.getFilmsByDirector(id, typeOfSort);
    }

    @Override
    public List<Film> getFilmsByDirectorOrTitle(String query, String param) {
        return filmRepository.getFilmsByDirectorOrTitle(query, param);
    }

    @Override
    public void delFilmById(int filmId) {
         filmRepository.delFilmById(filmId);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmRepository.getCommonFilms(userId, friendId);
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<Film> getTopPopularFilms(int count, int genreId, int year) {
        List<Film> top = filmRepository.findAll().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());

        if (genreId != 0 || year != 0) {
            return top.stream()
                    .filter(film -> {
                        if (genreId != 0) {
                            return film.getGenres().stream().anyMatch(genre -> genre.getId() == genreId);
                        } else {
                            return true;
                        }
                    })
                    .filter(film -> {
                        if (year != 0) {
                            return film.getReleaseDate().getYear() == year;
                        } else {
                            return true;
                        }
                    })
                    .collect(Collectors.toList());
        }
        return top;
    }

    @Override
    public List<Film> getFilmsByDirector(int id, String typeOfSort) {
        switch (typeOfSort) {
            case "year":
                return filmRepository.getFilmsByDirector(id).stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate))
                    .collect(Collectors.toList());
            case "likes":
                return filmRepository.getFilmsByDirector(id).stream()
                        .sorted(Comparator.comparingInt(o -> o.getLikes().size()))
                        .collect(Collectors.toList());
            default:
                return filmRepository.getFilmsByDirector(id);
        }
    }

    @Override
    public List<Film> getFilmsByDirectorOrTitleByQuery(String query, String param) {
        switch (param.toLowerCase()) {
            case "director":
                return getFilmsByDirectorQuery(query);
            case "title":
                return getFilmsByTitleQuery(query);
            case "title,director":
                return getFilmsByDirectorAndTitleQuery(query);
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public void delFilmById(int filmId) {
         filmRepository.delFilmById(filmId);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmRepository.getCommonFilms(userId, friendId);
    }

    private List<Film> getFilmsByTitleQuery(String query) {
        return filmRepository.findAll().stream()
                .filter(film -> film.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<Film> getFilmsByDirectorQuery(String query) {
            return findAll().stream()
                    .filter(film -> film.getDirectors().stream()
                            .anyMatch(director -> director.getName().toLowerCase().contains(query.toLowerCase())))
                    .collect(Collectors.toList());
        }

    private List<Film> getFilmsByDirectorAndTitleQuery(String query) {
        List<Film> matchDirectorFilms = getFilmsByDirectorQuery(query);
        List<Film> matchTitleFilms = getFilmsByTitleQuery(query);
        List<Film> result = new ArrayList<>();
        result.addAll(matchDirectorFilms);
        result.addAll(matchTitleFilms);
        return result;
    }
}

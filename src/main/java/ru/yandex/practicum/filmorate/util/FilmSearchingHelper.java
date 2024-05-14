package ru.yandex.practicum.filmorate.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.mappers.FilmRowMapper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmSearchingHelper {

    private final JdbcTemplate template;
    private final FilmRowMapper filmRowMapper;

    public FilmSearchingHelper(JdbcTemplate template, FilmRowMapper filmRowMapper) {
        this.template = template;
        this.filmRowMapper = filmRowMapper;
    }

    public List<Film> searchFilmByTitle(String query) {
        return template.query("select * from films order by id asc", filmRowMapper.mapper()).stream()
                .filter(film -> film.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Film> searchFilmByDirector(String query) {
        List<Film> filmsWithDirectors = template.query("select * from films order by id asc", filmRowMapper.mapper()).stream()
                .filter(film -> !film.getDirectors().isEmpty()).collect(Collectors.toList());
        Set<Film> setOfFilms = new LinkedHashSet<>();
        for (Film film : filmsWithDirectors) {
            for (Director director : film.getDirectors()) {
                if (director.getName().toLowerCase().contains(query.toLowerCase())) {
                    setOfFilms.add(film);
                }
            }
        }
        return new ArrayList<>(setOfFilms);
    }
}

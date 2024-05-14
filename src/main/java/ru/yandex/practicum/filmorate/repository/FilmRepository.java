package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.util.Checking;
import ru.yandex.practicum.filmorate.util.mappers.FilmRowMapper;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class FilmRepository {

    private JdbcTemplate template;
    private FilmRowMapper filmRowMapper;
    private Checking checking;
    private EventRepository events;

    public Film create(Film film) {
        log.info("На сохранение поступил фильм: id {}, name {}, release {}",
                film.getId(), film.getName(), film.getReleaseDate());
        template.update(
                "insert into films (name, description, release, duration) values(?, ?, ?, ?)",
                film.getName(),
                film.getDescription(),
                Date.from(film.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                film.getDuration());
        film.setId(template.queryForObject(
                "select max(id) as max from films",
                (rs, rowNum) -> rs.getInt("max")));
        log.info("saved film without rating and genres to table 'films'");
        if (film.getMpa() != null) {
            Integer ratingId = film.getMpa().getId();
            checking.valid(ratingId, "ratings");
            String ratingName = template.queryForObject(
                    "select name from ratings where id = ?",
                    (rs, rowNum) -> rs.getString("name"), ratingId);
            film.getMpa().setName(ratingName);
            template.update(
                    "update films set rating_id = ? where id = ?",
                    ratingId, film.getId());
            log.info("saved rating '{}' of the film to table 'films'", ratingName);
        }
        Set<Genre> genres = film.getGenres();
        if (!genres.isEmpty()) {
            genres.forEach(genre -> checking.valid(genre.getId(), "genres"));
            genres.forEach(genre -> genre.setName(template.queryForObject(
                    "select name from genres where id = ?",
                    (rs, rowNum) -> rs.getString("name"), genre.getId())));
            genres.forEach(genre -> template.update(
                    "insert into genres_films (genre_id, film_id) values (?, ?)",
                    genre.getId(), film.getId()));
            log.info("saved genres of the film to table 'genres_films'");
        }

        Set<Director> directors = film.getDirectors();
        if (!directors.isEmpty()) {
            directors.forEach(director -> checking.exist(director.getId(), "directors"));
            directors.forEach(director -> template.update(
                    "insert into directors_films (director_id, film_id) values (?, ?)",
                    director.getId(), film.getId()));
            directors.forEach(director -> director.setName(template.queryForObject(
                    "select name from directors where id = ?",
                    (rs, rowNum) -> rs.getString("name"), director.getId())));
        }
        log.info("Фильм с получил: id {} (name {}, release {})", film.getId(), film.getName(), film.getReleaseDate());
        return film;
    }

    public Film update(Film film) {
        checking.exist(film.getId(), "films");
        template.update(
                "update films set name = ?, description = ?, release = ?, duration = ? where id = ?",
                film.getName(),
                film.getDescription(),
                Date.from(film.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                film.getDuration(),
                film.getId());
        log.info("update film's: name, description, release, duration in table 'films'");
        if (film.getMpa() != null) {
            checking.exist(film.getMpa().getId(), "ratings");
            template.update(
                    "update films set rating_id = ? where id = ?",
                    film.getMpa().getId(), film.getId());
            String ratingName = template.queryForObject(
                    "select name from ratings where id = ?",
                    (rs, rowNum) -> rs.getString("name"), film.getMpa().getId());
            film.getMpa().setName(ratingName);
            log.info("update film's mpa in table 'films'");
        }
        Set<Genre> genres = film.getGenres();
        Set<Director> directors = film.getDirectors();

        template.update("delete from genres_films where film_id = ?", film.getId());
        if (!genres.isEmpty()) {
            genres.forEach(genre -> checking.valid(genre.getId(), "genres"));
            genres.forEach(genre -> template.update(
                    "insert into genres_films (genre_id, film_id) values (?, ?)",
                    genre.getId(), film.getId()));
            genres.forEach(genre -> genre.setName(template.queryForObject(
                    "select name from genres where id = ?",
                    (rs, rowNum) -> rs.getString("name"), genre.getId())));
            log.info("update film's genres in table 'genres_films'");
        }

        template.update("delete from directors_films where film_id = ?", film.getId());
        if (!directors.isEmpty()) {
            directors.forEach(director -> checking.exist(director.getId(), "directors"));
            directors.forEach(director -> template.update(
                    "insert into directors_films (director_id, film_id) values (?, ?)",
                    director.getId(), film.getId()));
            directors.forEach(director -> director.setName(template.queryForObject(
                    "select name from directors where id = ?",
                    (rs, rowNum) -> rs.getString("name"), director.getId())));
            log.info("update film's directors in table 'directors_films'");
        }
        return film;
    }

    public Film getById(Integer filmId) {
        checking.exist(filmId, "films");
        log.info("show film '{}'", filmId);
        return template.queryForObject("select * from films where id = ?",
                filmRowMapper.mapperWithAllParameters(), filmId);
    }

    public List<Film> findAll() {
        return template.query("select * from films order by id asc",
                filmRowMapper.mapperWithAllParameters());
    }

    public Film addLike(Integer filmId, Integer userId) {
        checking.exist(filmId, "films");
        checking.exist(userId, "users");
        template.update("insert into likes (film_id, user_id) values(?, ?)", filmId, userId);
        log.info("add user's '{}' like to film with '{}'", userId, filmId);
        events.addEvent(userId, "LIKE", filmId, "ADD");
        return getById(filmId);
    }

    public Film removeLike(Integer filmId, Integer userId) {
        checking.exist(filmId, "films");
        checking.exist(userId, "users");
        template.update("delete from likes where film_id = ? and user_id = ?", filmId, userId);
        log.info("remove user's '{}' like from film '{}'", userId, filmId);
        events.addEvent(userId, "LIKE", filmId, "REMOVE");
        return getById(filmId);
    }

    public List<Film> getFilmsByDirector(int id) {
        checking.exist(id, "directors");
        List<Integer> directorFilmIds = template.query(
                "select film_id as id from directors_films where director_id = ?",
                (rs, rowNum) -> rs.getInt("id"), id);
        List<Film> directorFilms = new ArrayList<>();
        directorFilmIds.forEach(filmId -> directorFilms.add(getById(filmId)));
        return directorFilms;
    }

    public void delFilmById(int filmId) {
        template.update("DELETE FROM public.films WHERE id=?", filmId);
        log.info("deleted film by id '{}'", filmId);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return template.query("SELECT film_id " +
                                "FROM likes " +
                                "WHERE user_id IN (?, ?) " +
                                "GROUP BY film_id " +
                                "HAVING COUNT(DISTINCT user_id) = 2 " +
                                "ORDER BY COUNT(*) DESC",
                        (rs, rowNum) -> rs.getInt("film_id"), userId, friendId)
                .stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }
}

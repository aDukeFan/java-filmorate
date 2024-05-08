package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RepeatException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class FilmRepository {

    private JdbcTemplate template;

    public Film create(Film film) {
        log.info("На сохранение поступил фильм: id {}, name {}, release {}", film.getId(), film.getName(), film.getReleaseDate());
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
            throwValidationExceptionForNonExistentId(ratingId, "ratings");
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
            genres.forEach(genre -> throwValidationExceptionForNonExistentId(
                    genre.getId(), "genres"));
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
            directors.forEach(director -> isDirectorInDirectorsTable(director.getId()));
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
        throwNotFoundExceptionForNonExistentId(film.getId(), "films");
        template.update(
                "update films set name = ?, description = ?, release = ?, duration = ? where id = ?",
                film.getName(),
                film.getDescription(),
                Date.from(film.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                film.getDuration(),
                film.getId());
        log.info("update film's: name, description, release, duration in table 'films'");
        if (film.getMpa() != null) {
            throwNotFoundExceptionForNonExistentId(film.getMpa().getId(), "ratings");
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
            genres.forEach(genre -> throwValidationExceptionForNonExistentId(genre.getId(), "genres"));
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
            directors.forEach(director -> isDirectorInDirectorsTable(director.getId()));
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
        throwNotFoundExceptionForNonExistentId(filmId, "films");
        Film film = template.queryForObject(
                "select name, description, release, duration from films where id = ?",
                (rs, rowNum) -> new Film()
                        .setId(filmId)
                        .setName(rs.getString("name"))
                        .setDescription(rs.getString("description"))
                        .setDuration(rs.getInt("duration"))
                        .setReleaseDate(rs.getDate("release").toLocalDate()), filmId);

        if (isSetFilmRating(filmId)) {
            Integer ratingId = template.queryForObject(
                    "select rating_id as mpa_id from films where id = ?",
                    (rs, rowNum) -> rs.getInt("mpa_id"), filmId);
            Rating mpa = template.queryForObject(
                    "select * from ratings where id = ?",
                    (rs, rowNum) -> new Rating()
                            .setName(rs.getString("name"))
                            .setId(rs.getInt("id")), ratingId);
            film.setMpa(mpa);
        }
        List<Integer> genresIds = template.query(
                "select genre_id as id from genres_films where film_id = ?",
                (rs, rowNum) -> rs.getInt("id"), filmId);
        if (!genresIds.isEmpty()) {
            List<Genre> genresList = new LinkedList<>();
            genresIds.forEach(genreId -> genresList.add(template.queryForObject(
                    "select * from genres where id = ?",
                    (rs, rowNum) -> new Genre()
                            .setId(rs.getInt("id"))
                            .setName(rs.getString("name")), genreId)));
            film.getGenres().addAll(genresList);
        }

        List<Director> filmDirectors = template.query(
                "select d.id, d.name " +
                        "from directors_films df " +
                        "join directors d on d.id = df.director_id " +
                        "where df.film_id = ?",
                (rs, rowNum) -> new Director()
                        .setId(rs.getInt("id"))
                        .setName(rs.getString("name")), filmId);
        film.getDirectors().addAll(filmDirectors);

        List<Integer> likesList = template.query(
                "select user_id as id from likes where film_id = ?",
                (rs, rowNum) -> rs.getInt("id"), filmId);
        if (!likesList.isEmpty()) {
            film.getLikes().addAll(likesList);
        }
        log.info("show film '{}'", filmId);
        return film;
    }

    public List<Film> findAll() {
        List<Integer> filmsId = template.query(
                "select id from films order by id asc",
                (rs, rowNum) -> rs.getInt("id"));
        List<Film> films = new ArrayList<>();
        filmsId.forEach(id -> films.add(getById(id)));
        return films;
    }

    public Film addLike(Integer filmId, Integer userId) {
        throwNotFoundExceptionForNonExistentId(filmId, "films");
        throwNotFoundExceptionForNonExistentId(userId, "users");
        if (isFilmLikedByUser(filmId, userId)) {
            throw new RepeatException("Film may be liked by user only one time");
        }
        template.update("insert into likes (film_id, user_id) values(?, ?)", filmId, userId);
        log.info("add user's '{}' like to film with '{}'", userId, filmId);
        return getById(filmId);
    }

    public Film removeLike(Integer filmId, Integer userId) {
        throwNotFoundExceptionForNonExistentId(filmId, "films");
        throwNotFoundExceptionForNonExistentId(userId, "users");
        if (!isFilmLikedByUser(filmId, userId)) {
            throw new RepeatException("No like by user with ID: " + userId);
        }
        template.update("delete from likes where film_id = ? and user_id = ?", filmId, userId);
        log.info("remove user's '{}' like from film '{}'", userId, filmId);
        return getById(filmId);
    }

    public List<Film> getTopPopularFilms(int count) {
        List<Integer> topIds = template.query(
                "select film_id as id from likes group by film_id order by count(user_id) desc limit(?)",
                (rs, rowNum) -> rs.getInt("id"), count);
        List<Film> topFilms = new ArrayList<>();
        topIds.forEach(id -> topFilms.add(getById(id)));
        log.info("show top {} films", count);
        return topFilms;
    }

    public List<Film> getFilmsByDirector(int id, String sortBy) {
        isDirectorInDirectorsTable(id);
        List<Integer> directorFilmIds = template.query(
                "select film_id as id from directors_films where director_id = ?",
                (rs, rowNum) -> rs.getInt("id"), id);
        List<Film> directorFilms = new ArrayList<>();
        directorFilmIds.forEach(filmId -> directorFilms.add(getById(filmId)));
        if (sortBy.equals("year")) {
            return directorFilms.stream().sorted(Comparator.comparing(Film::getReleaseDate)).collect(Collectors.toList());
        } else if (sortBy.equals("likes")) {
            return directorFilms.stream().sorted(Comparator.comparingInt(o -> o.getLikes().size())).collect(Collectors.toList());
        } else {
            return directorFilms;
        }
    }

    //select * from films f
    //join directors_films df on df.film_id = f.id and df.director_id = 1
    //order by f.release asc;
    //Описание задачи
    //В информацию о фильмах должно быть добавлено имя режиссёра. После этого должна появиться следующая функциональность.
    //
    //Вывод всех фильмов режиссёра, отсортированных по количеству лайков.
    //Вывод всех фильмов режиссёра, отсортированных по годам.
    //API
    //GET /films/director/{directorId}?sortBy=[year,likes]
    //
    //Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.

    private boolean isFilmLikedByUser(int filmId, int userId) {
        return Boolean.TRUE.equals(template.queryForObject(
                "select exists (select * from likes where film_id = ? and user_id = ?) as match",
                (rs, rowNum) -> rs.getBoolean("match"), filmId, userId));
    }

    private boolean isSetFilmRating(Integer id) {
        return Boolean.TRUE.equals(template.queryForObject(
                "select exists (select rating_id from films where rating_id is not null and id = ?) as match",
                (rs, rowNum) -> rs.getBoolean("match"), id));
    }

    private void throwNotFoundExceptionForNonExistentId(int id, String tableName) {
        String select = "select exists (select id as match from " + tableName + " where id = ?) as match";
        if (Boolean.FALSE.equals(template.queryForObject(select,
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No " + tableName + " with such ID: " + id);
        }
    }

    private void throwValidationExceptionForNonExistentId(int id, String tableName) {
        String select = "select exists (select id as match from " + tableName + " where id = ?) as match";
        if (Boolean.FALSE.equals(template.queryForObject(select,
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new ValidationException("No " + tableName + " with such ID: " + id);
        }
    }

    private void isDirectorInDirectorsTable(int id) {
        String select = "select exists (select id from directors where id = ?) as match";
        if (Boolean.FALSE.equals(template.queryForObject(select,
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No director with such ID: " + id);
        }
    }

}

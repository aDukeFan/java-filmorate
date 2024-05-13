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
        addEvent(userId, "LIKE", filmId, "ADD");
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
        addEvent(userId, "LIKE", filmId, "REMOVE");
        return getById(filmId);
    }

    public List<Film> getTopPopularFilms(int count, int genreId, int year) {
        List<Film> top = findAll().stream()
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

    public List<Film> getFilmsByDirector(int id, String sortBy) {
        isDirectorInDirectorsTable(id);
        List<Integer> directorFilmIds = template.query(
                "select film_id as id from directors_films where director_id = ?",
                (rs, rowNum) -> rs.getInt("id"), id);
        List<Film> directorFilms = new ArrayList<>();
        directorFilmIds.forEach(filmId -> directorFilms.add(getById(filmId)));
        if (sortBy.equals("year")) {
            return directorFilms.stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate))
                    .collect(Collectors.toList());
        } else if (sortBy.equals("likes")) {
            return directorFilms.stream()
                    .sorted(Comparator.comparingInt(o -> o.getLikes().size()))
                    .collect(Collectors.toList());
        } else {
            return directorFilms;
        }
    }

    public List<Film> getFilmsByDirectorOrTitle(String query, String param) {
        switch (param) {
            case "title":
                return searchFilmByTitle(query);
            case "director": {
                return searchFilmByDirector(query);
            }
            case "title,director": {
                List<Film> result = new ArrayList<>();
                result.addAll(searchFilmByDirector(query));
                result.addAll(searchFilmByTitle(query));
                return result;
            }
            default:
                return new ArrayList<>();
        }
    }

    private List<Film> searchFilmByTitle(String query) {
        return findAll().stream()
                .filter(film -> film.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<Film> searchFilmByDirector(String query) {
        List<Film> filmsWithDirectors = findAll().stream()
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

    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT film_id " +
                "FROM likes " +
                "WHERE user_id IN (?, ?) " +
                "GROUP BY film_id " +
                "HAVING COUNT(DISTINCT user_id) = 2 " +
                "ORDER BY COUNT(*) DESC";

        List<Integer> filmIds = template.query(sql, (rs, rowNum) -> rs.getInt("film_id"), userId, friendId);

        return filmIds.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

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


    public void delFilmById(int filmId) {
        template.update("DELETE FROM public.films WHERE id=?", filmId);
        log.info("deleted film by id '{}'", filmId);
    }

    private void addEvent(Integer userId, String eventType, Integer entityId, String operation) {
        template.update("INSERT INTO events " +
                        "(user_id, event_type, entity_id, operation, event_timestamp) " +
                        "VALUES(?,?,?,?,CURRENT_TIMESTAMP)",
                userId, eventType, entityId, operation);
    }
}

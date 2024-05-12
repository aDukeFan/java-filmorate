package ru.yandex.practicum.filmorate.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmRowMapper {

    private final JdbcTemplate template;

    public FilmRowMapper(JdbcTemplate template) {
        this.template = template;
    }

    public RowMapper<Film> filmRowMapper() {
        return ((rs, rowNum) ->
                new Film()
                        .setId(rs.getInt("id"))
                        .setName(rs.getString("name"))
                        .setDescription(rs.getString("description"))
                        .setReleaseDate(rs.getDate("release").toLocalDate())
                        .setDuration(rs.getInt("duration"))
                        .setLikes(setLikesUsersFromDb(rs.getInt("id")))
                        .setGenres(setGenresFromDb(rs.getInt("id")))
                        .setMpa(setRatingFromDb(rs.getInt("id")))
                        .setDirectors(setDirectorsFromDb(rs.getInt("id"))));
    }

    private Set<Integer> setLikesUsersFromDb(int filmId) {
        Set<Integer> likes = new LinkedHashSet<>();
        SqlRowSet sqlRowSet = template.queryForRowSet(
                "SELECT user_id " +
                        "FROM likes " +
                        "WHERE film_id = ?",
                filmId);
        while (sqlRowSet.next()) {
            likes.add(sqlRowSet.getInt("user_id"));
        }
        return likes;
    }

    private Set<Genre> setGenresFromDb(int filmId) {
        Set<Genre> genres = new LinkedHashSet<>();
        SqlRowSet sqlRowSet = template.queryForRowSet(
                "SELECT * " +
                        "FROM genres " +
                        "WHERE id IN (" +
                        "   SELECT genre_id " +
                        "FROM genres_films " +
                        "WHERE film_id = ?)",
                filmId);
        while (sqlRowSet.next()) {
            genres.add(
                    new Genre()
                            .setId(sqlRowSet.getInt("id"))
                            .setName(sqlRowSet.getString("name")));
        }
        return genres;
    }

    private Rating setRatingFromDb(int filmId) {
        return template.queryForObject(
                "SELECT * " +
                        "FROM ratings " +
                        "WHERE id = (" +
                        "SELECT rating_id " +
                        "FROM films " +
                        "WHERE id = ?)",
                mpaRowMapper(),
                filmId);
    }

    private RowMapper<Rating> mpaRowMapper() {
        return ((rs, rowNum) ->
                new Rating()
                        .setId(rs.getInt("id"))
                        .setName(rs.getString("name")));
    }

    private Set<Director> setDirectorsFromDb(int filmId) {
        Set<Director> directors = new HashSet<>();
        SqlRowSet sqlRowSet = template.queryForRowSet(
                "SELECT * " +
                        "FROM directors " +
                        "WHERE id IN (" +
                        "   SELECT director_id " +
                        "FROM directors_films " +
                        "WHERE film_id = ?)",
                filmId);
        while (sqlRowSet.next()) {
            directors.add(
                    new Director()
                            .setId(sqlRowSet.getInt("id"))
                            .setName(sqlRowSet.getString("name")));
        }
        return directors;
    }
}

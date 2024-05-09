package ru.yandex.practicum.filmorate.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;

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
                Film.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .likes(setLikesUsersFromDb(rs.getInt("id")))
                        .genres(setGenresFromDb(rs.getInt("id")))
                        .mpa(setRatingFromDb(rs.getInt("id")))
                        .directors(setDirectorsFromDb(rs.getInt("id")))
                        .build()
        );
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
            genres.add(Genre.builder()
                    .id(sqlRowSet.getInt("id"))
                    .name(sqlRowSet.getString("name"))
                    .build());
        }

        return genres.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toSet());
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
                Rating.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .build()
        );
    }
    
    private Set<Director> setDirectorsFromDb(int filmId){
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
            directors.add(Director.builder()
                    .id(sqlRowSet.getInt("id"))
                    .name(sqlRowSet.getString("name"))
                    .build());
        }

        return directors.stream()
                .sorted(Comparator.comparing(Director::getId))
                .collect(Collectors.toSet());
    }
}

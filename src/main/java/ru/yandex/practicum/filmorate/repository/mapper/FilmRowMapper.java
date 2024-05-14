package ru.yandex.practicum.filmorate.repository.mapper;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@AllArgsConstructor
public class FilmRowMapper {
    private final JdbcTemplate template;
    private RatingRowMapper ratingRowMapper;
    private GenreRowMapper genreRowMapper;
    private DirectorRowMapper directorRowMapper;

    public RowMapper<Film> getMapperWithAllParameters() {
        return ((rs, rowNum) ->
                new Film()
                        .setId(rs.getInt("id"))
                        .setName(rs.getString("name"))
                        .setDescription(rs.getString("description"))
                        .setReleaseDate(rs.getDate("release").toLocalDate())
                        .setDuration(rs.getInt("duration"))
                        .setLikes(getLikesUsersFromDb(rs.getInt("id")))
                        .setGenres(getGenresFromDb(rs.getInt("id")))
                        .setMpa(getRatingFromDb(rs.getInt("id")))
                        .setDirectors(getDirectorsFromDb(rs.getInt("id"))));
    }

    private Set<Integer> getLikesUsersFromDb(int filmId) {
        return new HashSet<>(template.queryForList("SELECT user_id " +
                "FROM likes " +
                "WHERE film_id = ?", Integer.class, filmId));
    }

    private Rating getRatingFromDb(int filmId) {
        return template.queryForObject("SELECT r.id, r.name " +
                "FROM ratings AS r " +
                "JOIN films AS f " +
                "ON f.rating_id = r.id " +
                "WHERE f.id = ?", ratingRowMapper.getMapper(), filmId);
    }

    private Set<Director> getDirectorsFromDb(int filmId) {
        return new HashSet<>(template.query("SELECT d.id AS id, d.name AS name " +
                "FROM directors AS d " +
                "JOIN directors_films AS df " +
                "ON df.director_id = d.id " +
                "WHERE df.film_id = ?", directorRowMapper.getMapper(), filmId));
    }

    private Set<Genre> getGenresFromDb(int filmId) {
        return new LinkedHashSet<>(template.query("SELECT g.id AS id, g.name AS name " +
                "FROM genres AS g " +
                "JOIN genres_films AS gf " +
                "ON gf.genre_id = g.id " +
                "WHERE gf.film_id = ?", genreRowMapper.getMapper(), filmId));
    }
}

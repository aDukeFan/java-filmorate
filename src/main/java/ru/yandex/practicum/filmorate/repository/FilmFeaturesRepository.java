package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Component
@AllArgsConstructor
public class FilmFeaturesRepository {

    private JdbcTemplate template;

    public Genre createGenre(Genre genre) {
        template.update(
                "insert into genres (name) values(?)",
                genre.getName());
        return template.queryForObject(
                "select * from genres where name = ?",
                genreRowMapper(), genre.getName());
    }

    public Genre updateGenre(Genre genre) {
        throwNotFoundExceptionForNonExistentId(genre.getId(), "genres");
        template.update(
                "update genres set name = ? where id = ?",
                genre.getName(), genre.getId());
        return genre;
    }

    public Genre getGenreById(int genreId) {
        throwNotFoundExceptionForNonExistentId(genreId, "genres");
        return template.queryForObject(
                "select * from genres where id = ?",
                genreRowMapper(), genreId);
    }

    public List<Genre> getAllGenres() {
        return template.query(
                "select * from genres order by id asc", genreRowMapper());
    }

    public void removeGenre(int genreId) {
        throwNotFoundExceptionForNonExistentId(genreId, "genres");
        template.update("delete from genres where id = ?", genreId);
    }

    public Rating createRating(Rating rating) {
        template.update(
                "insert into ratings (name) values(?)",
                rating.getName());
        return template.queryForObject(
                "select * from ratings where name = ?",
                ratingRowMapper(), rating.getName());
    }

    public Rating updateRating(Rating rating) {
        throwNotFoundExceptionForNonExistentId(rating.getId(), "ratings");
        template.update(
                "update ratings set name = ? where id = ?",
                rating.getName(),
                rating.getId());
        return rating;
    }

    public Rating getRatingById(int ratingId) {
        throwNotFoundExceptionForNonExistentId(ratingId, "ratings");
        return template.queryForObject(
                "select * from ratings where id = ?",
                ratingRowMapper(),
                ratingId);
    }

    public List<Rating> getAllRatings() {
        return template.query("select * from ratings order by id", ratingRowMapper());
    }

    public void removeRating(int ratingId) {
        throwNotFoundExceptionForNonExistentId(ratingId, "ratings");
        template.update("delete from ratings where id = ?", ratingId);

    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre()
                .setId(rs.getInt("id"))
                .setName(rs.getString("name"));
    }

    private RowMapper<Rating> ratingRowMapper() {
        return (rs, rowNum) -> new Rating()
                .setId(rs.getInt("id"))
                .setName(rs.getString("name"));
    }

    private void throwNotFoundExceptionForNonExistentId(int id, String tableName) {
        String select = "select exists (select id from " + tableName + " where id = ?) as match";
        if (Boolean.FALSE.equals(template.queryForObject(select,
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No users with such ID: " + id);
        }
    }
}



package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmFeaturesRepositoriesTest {

    private final JdbcTemplate template;

    @Test
    void testGetGenreById() {
        GenreRepository repository = new GenreRepository(template, null);
        Genre genre = new Genre().setId(7).setName("Экшн");
        repository.create(genre);
        Genre savedGenre = repository.getById(7);
        assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(genre);
    }

    @Test
    void getRatingById() {
        RatingRepository repository = new RatingRepository(template);
        Rating rating = new Rating().setId(6).setName("XYZ");
        repository.create(rating);
        Rating savedRating = repository.getById(6);
        assertThat(savedRating)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(rating);
    }
}
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
class FilmFeaturesRepositoryTest {

    private final JdbcTemplate template;

    @Test
    void testGetGenreById() {
        FilmFeaturesRepository repository = new FilmFeaturesRepository(template);
        Genre genre = new Genre().setId(7).setName("Экшн");
        repository.createGenre(genre);
        Genre savedGenre = repository.getGenreById(7);
        assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(genre);
    }

    @Test
    void getRatingById() {
        FilmFeaturesRepository repository = new FilmFeaturesRepository(template);
        Rating rating = new Rating().setId(6).setName("XYZ");
        repository.createRating(rating);
        Rating savedRating = repository.getRatingById(6);
        assertThat(savedRating)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(rating);
    }
}
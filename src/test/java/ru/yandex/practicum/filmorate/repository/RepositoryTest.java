package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RepositoryTest {

    private final JdbcTemplate template;

    @Test
    public void tests() {
        FilmRepository repository = new FilmRepository(template, null, null, null);
        List<Film> filmsBeforeSaved = make3Films(repository);
        List<Film> savedFilms = repository.findAll();
        assertThat(savedFilms)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmsBeforeSaved);
        Film film = repository.getById(1);
        Film savedFilm = repository.getById(1);
        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
        Film film1toUpdate = new Film()
                .setId(1)
                .setName("Фильм 1")
                .setDescription("Режиссерская версия")
                .setReleaseDate(LocalDate.of(2000, 11, 10))
                .setMpa(new Rating().setId(4).setName("R"))
                .setDuration(166);
        repository.update(film1toUpdate);
        Film updatedFilm = repository.getById(1);
        assertThat(updatedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1toUpdate);
        UserRepository userRepository = new UserRepository(template,
                null,
                null,
                null,
                null);
        List<User> usersBeforeSaved = make3Users(userRepository);
        repository.addLike(1, 1);
        repository.addLike(1, 2);
        repository.addLike(1, 3);
        repository.addLike(2, 1);
        repository.addLike(2, 2);
        repository.addLike(2, 3);
        repository.addLike(3, 1);
        Set<Integer> likesToAdd = new LinkedHashSet<>();
        likesToAdd.add(1);
        likesToAdd.add(2);
        repository.removeLike(1, 3);
        Set<Integer> savedLikes = repository.getById(1).getLikes();
        assertThat(savedLikes)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(likesToAdd);

        User savedUser = userRepository.getById(1);
        User user1BeforeSaved = usersBeforeSaved.get(0);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user1BeforeSaved);
        assertThat(userRepository.findAll())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(usersBeforeSaved);
        User user1toUpdate = new User()
                .setId(1)
                .setName("Антон Захарыч")
                .setLogin("antonio")
                .setBirthday(LocalDate.of(2000, 11, 1))
                .setEmail("ant@ya.ru");
        userRepository.update(user1toUpdate);
        assertThat(userRepository.getById(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user1toUpdate);
        userRepository.addFollow(1, 2);
        Set<Integer> friendsIds = new LinkedHashSet<>();
        friendsIds.add(2);
        assertThat(userRepository.getById(1).getFriends())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(friendsIds);
        userRepository.addFollow(1, 3);
        userRepository.addFollow(2, 1);
        userRepository.addFollow(2, 3);
        List<User> sameFriends = new ArrayList<>();
        sameFriends.add(userRepository.getById(3));
        assertThat(userRepository.getSameFollowers(1, 2))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(sameFriends);
        userRepository.removeFollowing(1, 3);
        userRepository.removeFollowing(1, 2);
        assertThat(userRepository.getById(1).getFriends()).isEmpty();
    }

    private List<Film> make3Films(FilmRepository filmRepository) {
        Film film1 = new Film()
                .setId(1)
                .setName("movie 1")
                .setDescription("о чем-то там")
                .setDuration(144)
                .setReleaseDate(LocalDate.of(2000, 10, 10))
                .setMpa(new Rating().setId(1).setName("G"));
        Film film2 = new Film()
                .setId(2)
                .setName("Фильм 2")
                .setDescription("о чем-то там")
                .setDuration(155)
                .setReleaseDate(LocalDate.of(2001, 10, 10))
                .setMpa(new Rating().setId(4).setName("R"));
        Film film3 = new Film()
                .setId(2)
                .setName("Фильм 3")
                .setDescription("о чем-то там")
                .setDuration(155)
                .setReleaseDate(LocalDate.of(2001, 10, 10));
        filmRepository.create(film1);
        filmRepository.create(film2);
        filmRepository.create(film3);
        return List.of(film1, film2, film3);
    }

    private List<User> make3Users(UserRepository userRepository) {
        User user1 = new User()
                .setName("Антошка")
                .setLogin("antoshka")
                .setBirthday(LocalDate.of(2000, 11, 1))
                .setEmail("ant@ya.ru");
        User user2 = new User()
                .setName("Володька")
                .setLogin("vovan")
                .setBirthday(LocalDate.of(1990, 1, 1))
                .setEmail("vovan@ya.ru");
        User user3 = new User()
                .setName("Наташа")
                .setLogin("natusik")
                .setEmail("na@ya.ru")
                .setBirthday(LocalDate.of(2005, 10, 1));
        userRepository.create(user1);
        userRepository.create(user2);
        userRepository.create(user3);
        return List.of(user1, user2, user3);
    }
}
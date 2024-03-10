package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private final FilmController controller = new FilmController();

    @Test
    public void shouldCreateAndUpdateValidUsersById() throws Exception {
        Film film = new Film(
                "Public Enemies", "about public enemies",
                LocalDate.of(2009, 12, 8), 200);

        Film savedFilm = controller.create(film);
        assertEquals(savedFilm.getName(), "Public Enemies");
        assertEquals(savedFilm.getDescription(), "about public enemies");
        assertEquals(savedFilm.getDuration(), 200);
        assertEquals(savedFilm.getReleaseDate(), LocalDate.of(2009, 12, 8));
        assertEquals(controller.findAll().size(), 1);

        Film filmToUpdate = new Film(
                "Johnny D.", "about public enemies",
                LocalDate.of(1985, 12, 29), 200);
        filmToUpdate.setId(1);
        Film updatedFilm = controller.update(filmToUpdate);
        assertEquals(updatedFilm.getName(), "Johnny D.");
        assertEquals(updatedFilm.getDescription(), "about public enemies");
        assertEquals(updatedFilm.getDuration(), 200);
        assertEquals(updatedFilm.getReleaseDate(), LocalDate.of(1985, 12, 29));
        assertEquals(controller.findAll().size(), 1);
    }

    @Test
    public void shouldThrowExceptions() throws Exception {
        Film film = new Film(
                "Public Enemies", "about public enemies",
                LocalDate.of(2009, 12, 8), 200);
        controller.create(film);
        film.setId(1);
        assertThrows(IllegalArgumentException.class, () -> controller.create(film));
        Film filmToUpdateWithWrongId = new Film(
                "Johnny D.", "about public enemies",
                LocalDate.of(1985, 12, 29), 200);
        filmToUpdateWithWrongId.setId(2);
        assertThrows(IllegalArgumentException.class, () -> controller.update(filmToUpdateWithWrongId));

    }


}
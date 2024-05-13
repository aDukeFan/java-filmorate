package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    User create(User user);

    User update(User user);

    User getById(int id);

    List<User> findAll();

    User addFollow(int userId, int friendId);

    User removeFollow(int userId, int friendId);

    List<User> getSameFriends(int userId, int friendId);

    List<User> getFriends(int userId);

    void delUserById(int userId);

    List<Film> getRecommendFilms(int userId);

    List<Event> getEventsByUserId(Integer id);
}

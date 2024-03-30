package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    User create(User user);

    User update(User user);

    User getById(int id);

    List<User> findAll();

    User addFriend(int userId, int friendId);

    User removeFriend(int userId, int friendId);

    List<User> getSameFriends(int userId, int friendId);

    List<User> getFriends(int userId);
}

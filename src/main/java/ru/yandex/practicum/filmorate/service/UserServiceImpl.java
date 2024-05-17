package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    @Override
    public User create(User user) {
        return repository.create(user);
    }

    @Override
    public User update(User user) {
        return repository.update(user);
    }

    @Override
    public User getById(int id) {
        return repository.getById(id);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User addFollow(int followingId, int followedId) {
        return repository.addFollow(followingId, followedId);
    }

    @Override
    public User removeFollow(int followingId, int followedId) {
        return repository.removeFollowing(followingId, followedId);
    }

    @Override
    public List<User> getSameFriends(int userId, int friendId) {
        return repository.getSameFollowers(userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        return repository.getFollowers(userId);
    }

    @Override
    public void delUserById(int userId) {
         repository.delUserById(userId);
    }

    @Override
    public List<Film> getRecommendFilms(int userId) {
        return repository.getRecommendations(userId);
    }

    @Override
    public List<Event> getEventsByUserId(Integer id) {
        return repository.getEventsByUserId(id);
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<Film> getRecommendations(int userId, String type) {
        int countOfAdviser = 10;
        int positiveGradeValue = 5;
        List<Integer> advisersIds = new ArrayList<>();
        Set<Film> recommendationsSet = new HashSet<>();
        switch (type) {
            case "likes":
                advisersIds.addAll(repository.getAdvisersIdsByLikes(userId, countOfAdviser));
                if (!advisersIds.isEmpty()) {
                    advisersIds.forEach(adviserId -> recommendationsSet.
                            addAll(repository
                                    .getRecommendationsFromAdviserByLikes(userId, adviserId)));
                }
            case "grades":
                advisersIds.addAll(repository.getAdvisersIdsByGrades(userId, countOfAdviser));
                if (!advisersIds.isEmpty()) {
                    advisersIds.forEach(adviserId -> recommendationsSet
                            .addAll(repository
                                    .getRecommendationsFromAdviserByGrades(userId, adviserId, positiveGradeValue)));
                }
        }
        return recommendationsSet.stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> getEventsByUserId(Integer id) {
        return repository.getEventsByUserId(id);
    }
}

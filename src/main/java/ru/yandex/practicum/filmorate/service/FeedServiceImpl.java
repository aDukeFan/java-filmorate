package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FeedRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedServiceImpl implements FeedService{

    FeedRepository repository;
    UserRepository userRepository;

    @Autowired
    public FeedServiceImpl(FeedRepository repository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    @Override
    public List<Event> get(Integer userId) {
        List<Event> events = new ArrayList<>();
        List<User> friends = userRepository.getFollowers(userId);
        for (User user1 : friends) {
            events.addAll(repository.get(user1.getId()));
        }
        return repository.get(userId);
    }
}

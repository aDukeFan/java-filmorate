package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.repository.FeedRepository;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedServiceImpl implements FeedService{

    FeedRepository repository;

    @Autowired
    public FeedServiceImpl(FeedRepository repository) {
        this.repository = repository;
    }

    @Override
    public Feed get(Integer userId) {
        return null;
    }
}

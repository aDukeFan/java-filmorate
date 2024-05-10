package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.service.FeedService;

@RequestMapping("/users")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedController {

    FeedService feedService;

    @Autowired
    public FeedController(@RequestBody FeedService feedService) {
        this.feedService = feedService;
    }

    public Feed get(Integer id) {
        return feedService.get(id);
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.FeedService;

import java.util.List;

@RequestMapping("/users")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedController {

    FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/{id}/feed")
    public List<Event> get(@RequestBody @PathVariable Integer id) {
        return feedService.get(id);
    }
}

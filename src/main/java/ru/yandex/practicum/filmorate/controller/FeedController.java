package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.FeedService;

import java.util.List;

@RequestMapping("/users")
@RestController
@AllArgsConstructor
public class FeedController {

    private FeedService feedService;

    @GetMapping("/{id}/feed")
    public List<Event> get(@PathVariable Integer id) {
        return feedService.get(id);
    }
}

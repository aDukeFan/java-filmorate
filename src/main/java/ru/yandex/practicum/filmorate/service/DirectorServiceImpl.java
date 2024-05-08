package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private DirectorRepository repository;

    @Override
    public Director create(Director director) {
        return repository.create(director);
    }

    @Override
    public Director update(Director director) {
        return repository.update(director);
    }

    @Override
    public Director getById(Integer id) {
        return repository.getById(id);
    }

    @Override
    public List<Director> getAll() {
        return repository.getAll();
    }

    @Override
    public void removeById(Integer id) {
        repository.removeById(id);
    }
}

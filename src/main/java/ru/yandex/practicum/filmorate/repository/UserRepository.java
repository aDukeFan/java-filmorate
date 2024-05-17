package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.util.ExistChecker;
import ru.yandex.practicum.filmorate.repository.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mapper.UserRowMapper;

import java.sql.PreparedStatement;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@AllArgsConstructor
@Slf4j
public class UserRepository {

    private JdbcTemplate template;
    private FilmRowMapper filmRowMapper;
    private UserRowMapper userRowMapper;
    private EventRepository events;
    private ExistChecker existChecker;

    public User create(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "insert into users (name, login, email, birthday) values(?, ?, ?, ?)",
                    new String[]{"id"});
            stmt.setString(1, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        return user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    public User update(User user) {
        existChecker.throwNotFountException(user.getId(), "users");
        template.update(
                "update users set name = ?, login = ?, email = ?, birthday = ? where id = ?",
                user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        log.info("update user '{}' in table 'users'", user.getId());
        return user;
    }


    public User getById(Integer id) {
        existChecker.throwNotFountException(id, "users");
        return template.queryForObject("select * from users where id = ?", userRowMapper.getMapperWithFriendsSet(), id);
    }

    public List<User> findAll() {
        return template.query("select * from users order by id asc", userRowMapper.getMapperWithFriendsSet());
    }

    public User addFollow(Integer userId, Integer friendId) {
        existChecker.throwNotFountException(userId, "users");
        existChecker.throwNotFountException(friendId, "users");
        if (!isFriends(userId, friendId)) {
            template.update(
                    "insert into follows (following_id, followed_id) values(?, ?)",
                    friendId, userId);
            log.info("subscribe user '{}' to user '{}'", userId, friendId);
            events.addEvent(userId, "FRIEND", friendId, "ADD");
        }
        return getById(userId);
    }

    public User removeFollowing(Integer userId, Integer friendId) {
        existChecker.throwNotFountException(userId, "users");
        existChecker.throwNotFountException(friendId, "users");
        if (isFriends(userId, friendId)) {
            template.update(
                    "delete from follows where following_id = ? and followed_id = ?",
                    friendId, userId);
            log.info("unsubscribe user '{}' from user '{}'", userId, friendId);
            events.addEvent(userId, "FRIEND", friendId, "REMOVE");
        }
        return getById(userId);
    }

    public List<User> getSameFollowers(Integer userId, Integer friendId) {
        existChecker.throwNotFountException(userId, "users");
        existChecker.throwNotFountException(friendId, "users");
        log.info("show same followers of user '{}' and user '{}'", userId, friendId);
        return template.query(
                "select * from users as u " +
                        "join follows as f on f.following_id = u.id and f.followed_id = ? " +
                        "join follows as friend_f on friend_f.following_id = u.id and friend_f.followed_id = ?",
                userRowMapper.getMapperWithFriendsSet(),
                friendId, userId);
    }

    public List<User> getFollowers(Integer userId) {
        existChecker.throwNotFountException(userId, "users");
        log.info("show followers of user '{}'", userId);
        return template.query(
                "select * from users as u " +
                        "join follows as f on f.following_id = u.id and f.followed_id = ?",
                userRowMapper.getMapperWithFriendsSet(),
                userId);
    }

    public List<Film> getRecommendations(Integer userId) {

        int userIdFromDb;
        int filmIdFromDb;
        Set<Integer> recommendedFilms = new HashSet<>();
        Map<Integer, HashSet<Integer>> usersAndLikes = new HashMap<>();
        SqlRowSet request = template.queryForRowSet("select * from likes");

        while (request.next()) {
            userIdFromDb = request.getInt("user_id");
            filmIdFromDb = request.getInt("film_id");
            if (!usersAndLikes.containsKey(userIdFromDb)) {
                usersAndLikes.put(userIdFromDb, new HashSet<>());
                usersAndLikes.get(userIdFromDb).add(filmIdFromDb);
            }
            usersAndLikes.get(userIdFromDb).add(filmIdFromDb);
        }
        Set<Integer> userLikesFilms = usersAndLikes.get(userId);

        for (HashSet<Integer> films : usersAndLikes.values()) {
            if (films.stream().anyMatch(userLikesFilms::contains)) {
                recommendedFilms.addAll(films);
            }
        }
        if (!recommendedFilms.isEmpty()) {
            recommendedFilms.removeAll(userLikesFilms);
            template.execute("drop table if exists recommended_films");
            template.execute("create local temporary table recommended_films (id int)");

            for (Integer film : recommendedFilms) {
                template.update("insert into recommended_films values(?)", film);
            }
            List<Film> finalRecommend = template.query(
                    "select * " +
                            "from films " +
                            "where id IN " +
                            "(select * " +
                            "from recommended_films)", filmRowMapper.getMapperWithAllParameters());

            template.execute("drop table if exists recommended_films");

            return finalRecommend;
        }
        return Collections.emptyList();
    }

    public void delUserById(int userId) {
        existChecker.throwNotFountException(userId, "users");
        template.update("DELETE FROM users WHERE id=?", userId);
        log.info("deleted user by id '{}'", userId);
    }

    public List<Event> getEventsByUserId(Integer id) {
        existChecker.throwNotFountException(id, "users");
        SqlRowSet sqlRowSet = template.queryForRowSet("SELECT * FROM events WHERE user_id = ?", id);
        List<Event> events = new ArrayList<>();
        while (sqlRowSet.next()) {
            Event event = new Event()
                    .setTimestamp(Objects.requireNonNull(sqlRowSet.getTimestamp("event_timestamp"))
                            .toLocalDateTime()
                            .toInstant(ZoneOffset.UTC)
                            .toEpochMilli())
                    .setEventType(sqlRowSet.getString("event_type"))
                    .setEventId(sqlRowSet.getInt("event_id"))
                    .setOperation(sqlRowSet.getString("operation"))
                    .setUserId(sqlRowSet.getInt("user_id"))
                    .setEntityId(sqlRowSet.getInt("entity_id"));
            events.add(event);
        }
        events.sort(Comparator.comparing(Event::getEventId));
        return events;
    }

    private boolean isFriends(int userId, int friendId) {
        return Boolean.TRUE.equals(template.queryForObject(
                "select exists (select * from follows where following_id = ? and followed_id = ?) as match",
                (rs, rowNum) -> rs.getBoolean("match"), friendId, userId));
    }
}

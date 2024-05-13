package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.util.mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.time.ZoneOffset;
import java.util.*;

@Component
@AllArgsConstructor
@Slf4j
public class UserRepository {

    private JdbcTemplate template;
    private FilmRowMapper filmRowMapper;
    private UserRowMapper userRowMapper;

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
        throwNotFoundExceptionForNonExistentUserId(user.getId());
        template.update(
                "update users set name = ?, login = ?, email = ?, birthday = ? where id = ?",
                user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        log.info("update user '{}' in table 'users'", user.getId());
        return user;
    }


    public User getById(Integer id) {
        throwNotFoundExceptionForNonExistentUserId(id);
        return template.queryForObject("select * from users where id = ?", userRowMapper.mapper(), id);
    }

    public List<User> findAll() {
        return template.query("select * from users order by id asc", userRowMapper.mapper());
    }

    public User addFollow(Integer userId, Integer friendId) {
        throwNotFoundExceptionForNonExistentUserId(userId);
        throwNotFoundExceptionForNonExistentUserId(friendId);
        if (!isUsersAreFriends(userId, friendId)) {
            template.update(
                    "insert into follows (following_id, followed_id) values(?, ?)",
                    friendId, userId);
            log.info("subscribe user '{}' to user '{}'", userId, friendId);
            addEvent(userId, "FRIEND", friendId, "ADD");
        }
        return getById(userId);
    }

    public User removeFollowing(Integer userId, Integer friendId) {
        throwNotFoundExceptionForNonExistentUserId(userId);
        throwNotFoundExceptionForNonExistentUserId(friendId);
        if (isUsersAreFriends(userId, friendId)) {
            template.update(
                    "delete from follows where following_id = ? and followed_id = ?",
                    friendId, userId);
            log.info("unsubscribe user '{}' from user '{}'", userId, friendId);
            addEvent(userId, "FRIEND", friendId, "REMOVE");
        }
        return getById(userId);
    }

    public List<User> getSameFollowers(Integer userId, Integer friendId) {
        throwNotFoundExceptionForNonExistentUserId(userId);
        throwNotFoundExceptionForNonExistentUserId(friendId);
        log.info("show same followers of user '{}' and user '{}'", userId, friendId);
        return template.query(
                "select * from users where id IN(" +
                        "select following_id from follows where followed_id = ? and following_id in (" +
                        "select following_id from follows where followed_id = ?))",
                userRowMapper.mapper(),
                friendId, userId);
    }

    public List<User> getFollowers(Integer userId) {
        throwNotFoundExceptionForNonExistentUserId(userId);
        log.info("show followers of user '{}'", userId);
        return template.query(
                "select * from users where id in (" +
                        "select distinct following_id from follows where followed_id = ?)",
                userRowMapper.mapper(),
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
                            "from recommended_films)", filmRowMapper.mapper());

            template.execute("drop table if exists recommended_films");

            return finalRecommend;
        }
        return Collections.emptyList();
    }

    private void throwNotFoundExceptionForNonExistentUserId(int id) {
        if (Boolean.FALSE.equals(template.queryForObject(
                "select exists (select id from users where id = ?) as match",
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No users with such ID: " + id);
        }
    }

    public void delUserById(int userId) {
        throwNotFoundExceptionForNonExistentUserId(userId);
        template.update("DELETE FROM users WHERE id=?", userId);
        log.info("deleted user by id '{}'", userId);
    }

    public List<Event> getEventsByUserId(Integer id) {
        throwNotFoundExceptionForNonExistentUserId(id);
        SqlRowSet sqlRowSet = template.queryForRowSet("SELECT * FROM events WHERE user_id = ?", id);
        List<Event> events = new ArrayList<>();
        while (sqlRowSet.next()) {
            Event event = new Event()
                    .setTimestamp(sqlRowSet.getTimestamp("event_timestamp")
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

    private void addEvent(Integer userId, String eventType, Integer entityId, String operation) {
        template.update("INSERT INTO events " +
                        "(user_id, event_type, entity_id, operation, event_timestamp) " +
                        "VALUES(?,?,?,?,CURRENT_TIMESTAMP)",
                userId, eventType, entityId, operation);
    }

    private boolean isUsersAreFriends(int userId, int friendId) {
        return Boolean.TRUE.equals(template.queryForObject(
                "select exists (select * from follows where following_id = ? and followed_id = ?) as match",
                (rs, rowNum) -> rs.getBoolean("match"), friendId, userId));
    }
}

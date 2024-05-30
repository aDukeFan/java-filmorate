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
import ru.yandex.practicum.filmorate.repository.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.repository.util.ExistChecker;

import java.sql.PreparedStatement;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
        boolean isMatchExists = Boolean.TRUE.equals(template.queryForObject("select exists " +
                "(select user_id from likes where film_id in " +
                "(select film_id from likes where user_id = ?) and user_id !=?) " +
                "as match", Boolean.class, userId, userId));
        if (isMatchExists) {
            Integer adviserId = template.queryForObject("select user_id from likes " +
                    "where film_id in " +
                    "(select film_id from likes where user_id = ?) " +
                    "and user_id !=? " +
                    "group by user_id " +
                    "order by count(*) desc " +
                    "limit(1)", Integer.class, userId, userId);
            return template.query("select * from films as f " +
                            "join likes as l on l.film_id = f.id " +
                            "where l.user_id = ? " +
                            "and l.film_id not in " +
                            "(select film_id from likes where user_id = ?)",
                    filmRowMapper.getMapperWithAllParameters(), adviserId, userId);
        } else {
            return List.of();
        }
    }

    public List<Film> getRecommendFilmsByGrade(int userId, int positiveGradeValue) {
        boolean isMatchExists = Boolean.TRUE.equals(template.queryForObject("select exists " +
                "(select user_id from grades where grade_value in " +
                "(select grade_value from grades where user_id = ?) and user_id != ?) " +
                "as match", Boolean.class, userId, userId));
        if (isMatchExists) {
            Integer adviserId = template.queryForObject("select user_id from grades " +
                    "where grade_value in " +
                    "(select grade_value from grades where user_id = ?) " +
                    "and user_id != ? " +
                    "group by user_id " +
                    "order by count(*) desc " +
                    "limit(1)", Integer.class, userId, userId);
            return template.query("select * from films f " +
                            "join grades as g on g.film_id = f.id " +
                            "where g.user_id = ? " +
                            "and g.film_id not in " +
                            "(select film_id from grades where user_id = ?) " +
                            "and g.grade_value > ?",
                    filmRowMapper.getMapperWithAllParameters(), adviserId, userId, positiveGradeValue);
        } else {
            return List.of();
        }
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

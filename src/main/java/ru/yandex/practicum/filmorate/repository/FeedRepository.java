package ru.yandex.practicum.filmorate.repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

import java.time.ZoneOffset;
import java.util.*;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class FeedRepository {

    JdbcTemplate template;

    public List<Event> get(Integer id) {
        throwNotFoundExceptionForNonExistentUserId(id);
        //String sql = "SELECT * FROM events WHERE user_id IN (SELECT following_id FROM follows WHERE followed_id = ?)";
        //String sqlNew = "SELECT * FROM events WHERE user_id IN (SELECT followed_id FROM follows WHERE following_id = ?)"; // поменяно местами followed_id following_id от sql
        String sql1 = "select * from events where user_id = ?";
        String sql = "SELECT * FROM events WHERE user_id = ? OR user_id IN (select following_id from follows where followed_id = ?)";
        String sqlNew = "select * from events where user_id IN (select following_id from follows where followed_id = ?)";
        SqlRowSet sqlRowSet = template.queryForRowSet(sql1, id);
        List<Event> events = new ArrayList<>();
        while (sqlRowSet.next()) {
            Event event = new Event()
                    .setTimestamp(sqlRowSet.getTimestamp("event_timestamp").toLocalDateTime()
                            .toInstant(ZoneOffset.UTC).toEpochMilli())
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

    public void recordAddEvent(Integer userId, String eventType, Integer entityId, String operation) {

        String sql = "insert into events(user_id, event_type, entity_id, operation, event_timestamp) " +
                "values(?,?,?,?,CURRENT_TIMESTAMP)";
        template.update(sql, userId, eventType, entityId, operation);
    }

    private void throwNotFoundExceptionForNonExistentUserId(int id) {
        if (Boolean.FALSE.equals(template.queryForObject(
                "select exists (select id from users where id = ?) as match",
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No users with such ID: " + id);
        }
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "select * from events";
        SqlRowSet sqlRowSet = template.queryForRowSet(sql);
            while (sqlRowSet.next()) {
                Event event = new Event()
                        .setTimestamp(sqlRowSet.getTimestamp("event_timestamp").toLocalDateTime()
                                .toInstant(ZoneOffset.UTC).toEpochMilli())
                        .setEventType(sqlRowSet.getString("event_type"))
                        .setEventId(sqlRowSet.getInt("event_id"))
                        .setOperation(sqlRowSet.getString("operation"))
                        .setUserId(sqlRowSet.getInt("user_id"))
                        .setEntityId(sqlRowSet.getInt("entity_id"));
                events.add(event);
        }
        return events;
    }

}

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

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class FeedRepository {

    JdbcTemplate template;

    public List<Event> get(Integer id) {
        throwNotFoundExceptionForNonExistentUserId(id);
        SqlRowSet sqlRowSet = template.queryForRowSet("SELECT * FROM events " +
                "WHERE user_id IN " +
                "(SELECT following_id FROM follows " +
                "WHERE followed_id = ?)", id);
        List<Event> events = new ArrayList<>();
        while (sqlRowSet.next()) {
            Event event = new Event()
                    .setTimeStamp(sqlRowSet.getTimestamp("event_timestamp"))
                    .setEventType(sqlRowSet.getString("event_type"))
                    .setEventId(sqlRowSet.getInt("event_id"))
                    .setOperation(sqlRowSet.getString("operation"))
                    .setUserId(sqlRowSet.getInt("user_id"))
                    .setEntityId(sqlRowSet.getInt("entity_id"));
            events.add(event);
        }
        return events;
    }

    public void recordAddEvent(Integer userId, String eventType, Integer entityId, String operation) {
        String sql = "insert into events (user_id, event_type, entity_id, operation) values(?,?,?,?)";
        template.update(sql, userId, eventType, entityId, operation);
    }

    private void throwNotFoundExceptionForNonExistentUserId(int id) {
        if (Boolean.FALSE.equals(template.queryForObject(
                "select exists (select id from users where id = ?) as match",
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No users with such ID: " + id);
        }
    }

}

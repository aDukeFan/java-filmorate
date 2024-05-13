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
public class EventRepository {

    JdbcTemplate template;

    public List<Event> get(Integer id) {
        throwNotFoundExceptionForNonExistentUserId(id);
        String sql = "select * from events where user_id = ?";
        SqlRowSet sqlRowSet = template.queryForRowSet(sql, id);
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

    public void addEvent(Integer userId, Integer entityId, String eventType, String operation) {
        template.update("insert into events (user_id, entity_id, event_type, operation, event_timestamp) " +
                "values(?, ?, ?, ?, CURRENT_TIMESTAMP)", userId, entityId, eventType, operation);
    }

    private void throwNotFoundExceptionForNonExistentUserId(int id) {
        if (Boolean.FALSE.equals(template.queryForObject(
                "select exists (select id from users where id = ?) as match",
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No users with such ID: " + id);
        }
    }
}

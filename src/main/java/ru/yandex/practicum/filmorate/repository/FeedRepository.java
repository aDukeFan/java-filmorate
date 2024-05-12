package ru.yandex.practicum.filmorate.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FeedRepository {

    JdbcTemplate template;

    @Autowired
    public FeedRepository(JdbcTemplate template) {
        this.template = template;
    }

    public List<Event> get(Integer id) {
        String sql = "select * from events where user_id = ?";
        SqlRowSet sqlRowSet = template.queryForRowSet(sql, id);
        List<Event> events = new ArrayList<>();
        while (sqlRowSet.next()) {
            Event event = Event.builder()
                    .timeStamp(sqlRowSet.getTimestamp("timestamp"))
                    .userId(sqlRowSet.getInt("user_id"))
                    .eventType(sqlRowSet.getString("event_name"))
                    .operation(sqlRowSet.getString("operation"))
                    .eventId(sqlRowSet.getInt("id"))
                    .entityId(sqlRowSet.getInt("entity_id"))
                    .build();
            events.add(event);
        }
        return events;
    }

    public void recordAddEvent(Integer userId, String eventType, Integer entityId, String operation) {
        String sql = "insert into events(user_id, event_type, entity_id, operation) values(?,?,?,?)";
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

package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventRepository {

    private final JdbcTemplate template;

    public void addEvent(Integer userId, String eventType, Integer entityId, String operation) {
        template.update("INSERT INTO events " +
                        "(user_id, event_type, entity_id, operation, event_timestamp) " +
                        "VALUES(?,?,?,?,CURRENT_TIMESTAMP)",
                userId, eventType, entityId, operation);
    }
}

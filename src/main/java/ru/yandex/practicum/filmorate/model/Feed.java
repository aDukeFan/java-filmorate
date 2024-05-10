package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class Feed {
    Timestamp timeStamp;
    Integer userId;
    String eventType;
    String operation;
    Integer eventId;
    Integer entityId;
}

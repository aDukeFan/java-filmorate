package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class Event {
    Timestamp timeStamp;
    Integer userId;
    String eventType;
    String operation;
    Integer eventId;
    Integer entityId;
}

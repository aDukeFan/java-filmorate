package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Accessors(chain = true)
public class Event {
    Timestamp timeStamp;
    Integer userId;
    String eventType;
    String operation;
    Integer eventId;
    Integer entityId;
}

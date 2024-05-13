package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Accessors(chain = true)
public class Event {
    long timestamp;
    Integer userId;
    String eventType;
    String operation;
    Integer eventId;
    Integer entityId;
}

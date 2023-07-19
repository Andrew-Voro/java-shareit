package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;


@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Item {

    Long id;

    String name;

    String description;

    Boolean available;

    User owner;

    Long requestId;
}

package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ItemDto implements Serializable {
    Long id;
    @NonNull
    String name;
    @NonNull
    String description;
    @NonNull
    Boolean available;
    Long owner;
    Long request;
}

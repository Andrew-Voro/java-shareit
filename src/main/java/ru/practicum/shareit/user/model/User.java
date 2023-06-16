package ru.practicum.shareit.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;


@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @EqualsAndHashCode.Exclude
    Long id;
    @EqualsAndHashCode.Exclude
    String name;
    @Email
    @NonNull
    String email;
}
